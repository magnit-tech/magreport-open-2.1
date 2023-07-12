package ru.magnit.magreportbackend.service.jobengine.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTypeEnum;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobStatusEnum;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.jobengine.CacheRow;
import ru.magnit.magreportbackend.dto.inner.jobengine.ReportRunnerData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobTupleData;
import ru.magnit.magreportbackend.dto.request.reportjob.ExcelReportRequest;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.mapper.reportjob.ReportReaderDataMerger;
import ru.magnit.magreportbackend.mapper.reportjob.ReportWriterDataMerger;
import ru.magnit.magreportbackend.service.ReportJobService;
import ru.magnit.magreportbackend.service.dao.ConnectionPoolManager;
import ru.magnit.magreportbackend.service.domain.ExportDataInExternalTableDomainService;
import ru.magnit.magreportbackend.service.domain.FilterReportDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.SecurityFilterDomainService;
import ru.magnit.magreportbackend.service.domain.StompMessageService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.service.jobengine.JobEngine;
import ru.magnit.magreportbackend.service.jobengine.ReportReaderFactory;
import ru.magnit.magreportbackend.service.jobengine.ReportWriterFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import static ru.magnit.magreportbackend.domain.dataset.DataSetTypeEnum.PROCEDURE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobEngineImpl implements JobEngine, InitializingBean {
    private final JobDomainService jobDomainService;
    private final FilterReportDomainService filterReportDomainService;
    private final ReportReaderFactory readerFactory;
    private final ReportWriterFactory writerFactory;
    private final SecurityFilterDomainService securityFilterService;
    private final UserDomainService userService;
    private final ExportDataInExternalTableDomainService exportDataInExternalTableDomainService;
    private final ReportJobService reportJobService;
    private final StompMessageService stompMessageService;

    private final ReportReaderDataMerger readerDataMerger;
    private final ReportWriterDataMerger writerDataMerger;
    private final ConnectionPoolManager connectionPoolManager;
    private int numActiveThreads = Integer.MAX_VALUE;
    private int numActiveJobs = Integer.MAX_VALUE;
    private int numActiveExportThreads = Integer.MAX_VALUE;
    private int numActiveExportJobs = Integer.MAX_VALUE;

    @Qualifier("JobEngineTaskExecutor")
    private final ThreadPoolTaskExecutor taskExecutor;

    @Qualifier("JobEngineTaskExport")
    private final ThreadPoolTaskExecutor taskExport;

    @Value("${magreport.jobengine.max-rows-excel}")
    private Long maxRowsExcel;

    // Active jobs
    private final Map<Long, ReportRunnerData> reportRunners = new HashMap<>();

    private final Map<Long, CompletableFuture<Void>> exportReportRunners = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        jobDomainService.refreshStatusesAfterRestart();
    }

    /**
     * Start async job to save report into cache
     *
     * @param job job parameters
     */
    private void startJob(ReportJobData job) {

        var cache = new LinkedBlockingDeque<CacheRow>(1000);

        var reader = readerFactory.getReader(readerDataMerger.merge(job, cache));
        var writer = writerFactory.getWriter(writerDataMerger.merge(job, cache, reader::isFinished));

        taskExecutor.execute(reader);
        taskExecutor.execute(writer);

        reportRunners.put(job.id(), new ReportRunnerData(reader, writer));
    }

    /**
     * Main processor of job queue
     */
    @Override
    @Scheduled(initialDelayString = "${magreport.jobengine.initial-delay}", fixedDelayString = "${magreport.jobengine.fixed-delay}")
    public void processJobQueue() {
        // update statuses of running jobs
        checkJobStatuses();
        checkJobExportStatuses();
        jobDomainService.restartPendingJobs();

        // Check available threads

        // if threads available
        if (taskExecutor.getActiveCount() != numActiveThreads) {
            numActiveThreads = taskExecutor.getActiveCount();
            log.debug("Number of active threads: " + numActiveThreads);
        }

        if (taskExecutor.getActiveCount() == taskExecutor.getMaxPoolSize()) return;

        // get next scheduled job
        var scheduledJob = jobDomainService.getNextScheduledJob();
        if (scheduledJob == null) return;

        if (!connectionPoolManager.isConnectionAvailable(scheduledJob.dataSource())) {
            jobDomainService.setJobStatus(scheduledJob.id(), ReportJobStatusEnum.PENDING_DB_CONNECTION, 0L, "There is no free connection in connection pool.");
            return;
        }

        try {
            // decode fields values
            filterReportDomainService.decodeFieldsValues(scheduledJob);

            getSecurityFilterSettings(scheduledJob);

            checkCountItemFilters(scheduledJob);

            if (PROCEDURE.equalsIsLong(scheduledJob.dataSetTypeId()))
                exportDataInExternalTableDomainService.exportData(scheduledJob);

            // and start it out
            startJob(scheduledJob);
            final var query = readerFactory.getQueryBuilder(scheduledJob.dataSource().type()).getQuery(scheduledJob);
            jobDomainService.setJobStatus(scheduledJob.id(), ReportJobStatusEnum.RUNNING, 0L, null, query);
        } catch (Exception ex) {
            log.error("\n\nFailed to start Job:" + scheduledJob.id() + "\nError message: " + ex.getMessage(), ex);
            jobDomainService.setJobStatus(scheduledJob.id(), ReportJobStatusEnum.FAILED, 0L, ex.getMessage());
        }
    }

    private void getSecurityFilterSettings(ReportJobData scheduledJob) {
        final var userRoles = userService.getUserRoles(scheduledJob.userName(), "CORP", null)
                .stream()
                .map(RoleView::getId)
                .collect(Collectors.toSet());

        final var effectiveSettings = securityFilterService
                .getEffectiveSettingsForReport(
                        scheduledJob.reportId(),
                        userRoles);

        scheduledJob.securityFilterParameters().addAll(effectiveSettings);
    }

    @Scheduled(cron = "${magreport.jobengine.history-clear-schedule}")
    private void clearJobHistory() {
        jobDomainService.deleteOldJobs();
    }

    private void checkJobStatuses() {

        if (numActiveJobs != reportRunners.size()) {
            numActiveJobs = reportRunners.size();
            log.debug("Number of active jobs: " + numActiveJobs);
        }

        cancelJobs();

        // Get finished reports
        final var finishedReports = reportRunners.entrySet()
                .stream()
                .filter(o -> o.getValue().writer().isFinished())
                .toList();

        // Stop readers if writers are finished
        finishedReports
                .stream()
                .filter(o -> !o.getValue().reader().isFinished())
                .forEach(o -> o.getValue().reader().cancel());

        // Finish stopped reports
        finishedReports.forEach(this::finishJob);
    }

    private void cancelJobs() {

        final var jobs = jobDomainService.getJobs(new ArrayList<>(reportRunners.keySet()));

        jobs
                .stream()
                .filter(job -> job.getStatus() == ReportJobStatusEnum.CANCELING)
                .forEach(job -> {
                    reportRunners.get(job.getId()).writer().cancel();
                    reportRunners.get(job.getId()).reader().cancel();
                });
    }

    private void finishJob(Map.Entry<Long, ReportRunnerData> reportRunnerEntry) {

        log.debug("Job " + reportRunnerEntry.getKey() + " is finished.");
        if (reportRunnerEntry.getValue().reader().isFailed() || reportRunnerEntry.getValue().writer().isFailed()) {
            jobDomainService.setJobStatus(reportRunnerEntry.getKey(), ReportJobStatusEnum.FAILED, 0L, getErrorDescription(reportRunnerEntry));
        } else {
            if (reportRunnerEntry.getValue().reader().isCanceled() || reportRunnerEntry.getValue().writer().isCanceled()) {
                jobDomainService.setJobStatus(reportRunnerEntry.getKey(), ReportJobStatusEnum.CANCELED, 0L);
            } else {

                if (reportRunnerEntry.getValue().reader().getRowCount() <= maxRowsExcel) {

                    jobDomainService.setJobStatus(reportRunnerEntry.getKey(), ReportJobStatusEnum.EXPORT, reportRunnerEntry.getValue().reader().getRowCount());

                    exportReportRunners.put(reportRunnerEntry.getKey(), CompletableFuture.runAsync(() -> {
                        try {
                            reportJobService.saveExcelReport(new ExcelReportRequest().setId(reportRunnerEntry.getKey()));
                        } catch (Exception ex) {
                            log.error("Error while trying to save report: " + ex.getMessage(), ex);
                        }
                    }, taskExport));
                } else {
                    jobDomainService.setJobStatus(reportRunnerEntry.getKey(), ReportJobStatusEnum.COMPLETE, reportRunnerEntry.getValue().reader().getRowCount());
                }
            }
        }

        reportRunners.remove(reportRunnerEntry.getKey());
    }

    private String getErrorDescription(Map.Entry<Long, ReportRunnerData> reportRunnerEntry) {
        return reportRunnerEntry.getValue().reader().isFailed() ?
                reportRunnerEntry.getValue().reader().getErrorDescription() :
                reportRunnerEntry.getValue().writer().getErrorDescription();
    }

    private void checkJobExportStatuses() {

        if (taskExport.getActiveCount() != numActiveExportThreads) {
            numActiveExportThreads = taskExport.getActiveCount();
            log.debug("Number of active exporting threads: " + numActiveExportThreads);
        }

        var finish = exportReportRunners.entrySet()
                .stream()
                .filter(o -> o.getValue().isDone())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        finish.forEach((key, value) -> {
            var job = jobDomainService.getJob(key);
            if (value.isCompletedExceptionally())
                jobDomainService.setJobStatus(key, ReportJobStatusEnum.FAILED, Long.max(0, job.getRowCount()));
            else
                jobDomainService.setJobStatus(key, ReportJobStatusEnum.COMPLETE, Long.max(0, job.getRowCount()));
            exportReportRunners.remove(key);

            sendMessageToUser(key);
        });

        if (exportReportRunners.size() != numActiveExportJobs) {
            numActiveExportJobs = exportReportRunners.size();
            log.debug("Number of active export jobs:" + numActiveExportJobs);
        }
    }


    private void sendMessageToUser(Long idJob) {
        var job = jobDomainService.getJob(idJob);
        stompMessageService.sendReportStatus(job.getUser().name(), job);
    }

    private void checkCountItemFilters(ReportJobData reportJobData) {

        reportJobData.parameters().stream()
                .filter(f ->
                        f.filterType().equals(FilterTypeEnum.TOKEN_INPUT) || f.filterType().equals(FilterTypeEnum.VALUE_LIST) ||
                                f.filterType().equals(FilterTypeEnum.VALUE_LIST_UNBOUNDED) || f.filterType().equals(FilterTypeEnum.DATE_RANGE))
                .forEach(f -> {

                    if (f.maxCountItems() == null) return;

                    var currentCountItems = 0L;
                    for (ReportJobTupleData reportJobTupleData : f.fieldValues()) {
                        long size = reportJobTupleData.fieldValues().size();
                        currentCountItems += size;
                    }

                    if (f.maxCountItems() <  currentCountItems && !f.filterType().equals(FilterTypeEnum.DATE_RANGE)) {
                        throw new InvalidParametersException(String.format("Превышен лимит количества элементов в фильтре '%s': %s", f.filterName(), f.maxCountItems()));
                    }

                    if (f.filterType().equals(FilterTypeEnum.DATE_RANGE)) {

                        f.fieldValues().forEach(field -> {
                            var date1 = LocalDate.parse(field.fieldValues().get(0).value()).plusDays(f.maxCountItems());
                            var date2 = LocalDate.parse(field.fieldValues().get(1).value());

                            if (date1.isBefore(date2)) {
                                throw new InvalidParametersException(String.format("Превышен лимит диапазона дат в фильтре '%s': %s", f.filterName(), f.maxCountItems()));
                            }

                        });
                    }
                });

    }

}
