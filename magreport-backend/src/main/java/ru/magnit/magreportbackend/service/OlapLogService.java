package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.DatePeriodRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapRequestGeneralInfo;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobBaseStats;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapLogDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlapLogService {

    private final OlapLogDomainService olapLogDomainService;

    private final JobDomainService jobDomainService;

    private final ReportDomainService reportDomainService;

    public List<OlapRequestGeneralInfo> getGeneralLogInfo(DatePeriodRequest request) {
        final var logEntries = olapLogDomainService.getLogEntriesForPeriod(request);
        final var jobStats = getJobStats();

        var result =  logEntries
            .stream()
            .map(logEntry -> new OlapRequestGeneralInfo(
                logEntry.getUser(),
                getJobId(logEntry.getRequest()),
                getReportId(logEntry.getRequest()),
                "",
                "",
                logEntry.getUrl(),
                0,
                logEntry.getDateTime(),
                logEntry.getDateTime()
                ))
            .map(logEntry -> extractInfo(logEntry, jobStats))
            .toList();

        var distinctResult = result.stream().distinct().collect(Collectors.toMap(Function.identity(), OlapRequestGeneralInfo::requestCount));
        var minFirstDateTimes = result.stream().distinct().collect(Collectors.toMap(Function.identity(), OlapRequestGeneralInfo::firstDateTime));
        var maxLastDatesTimes = result.stream().distinct().collect(Collectors.toMap(Function.identity(), OlapRequestGeneralInfo::lastDateTime));

        result.forEach(entry -> {
            var count = distinctResult.get(entry);
            var minFirstDateTime = minFirstDateTimes.get(entry);
            var maxLastDateTime = maxLastDatesTimes.get(entry);
            if (entry.firstDateTime().isBefore(minFirstDateTime)){
                minFirstDateTimes.remove(entry);
                minFirstDateTimes.put(entry, entry.firstDateTime());
                minFirstDateTime = entry.firstDateTime();
            }
            if (entry.lastDateTime().isAfter(maxLastDateTime)){
                maxLastDatesTimes.remove(entry);
                maxLastDatesTimes.put(entry, entry.lastDateTime());
                maxLastDateTime= entry.lastDateTime();
            }
            distinctResult.remove(entry);
            distinctResult.put(new OlapRequestGeneralInfo(
                entry.userName(),
                entry.reportJobId(),
                entry.reportId(),
                entry.reportName(),
                entry.reportJobOwner(),
                entry.requestURL(),
                count + 1,
                minFirstDateTime,
                maxLastDateTime
            ), count + 1);
        });

        return distinctResult.keySet().stream().toList();
    }

    private Map<Long, ReportJobBaseStats> getJobStats() {
        return jobDomainService.getAllDistinctJobStats()
            .stream()
            .collect(Collectors.toMap(ReportJobBaseStats::reportJobId, Function.identity()));
    }

    private OlapRequestGeneralInfo extractInfo(OlapRequestGeneralInfo source, Map<Long, ReportJobBaseStats> jobStats) {

        if (source.reportJobId() == null && source.reportId() == null) return source;
        if (source.reportJobId() != null && !jobStats.containsKey(source.reportJobId())) return source;

        if (source.reportJobId() != null) {
            return new OlapRequestGeneralInfo(
                source.userName(),
                source.reportJobId(),
                jobStats.get(source.reportJobId()).reportId(),
                jobStats.get(source.reportJobId()).reportName(),
                jobStats.get(source.reportJobId()).userName(),
                source.requestURL(),
                source.requestCount(),
                source.firstDateTime(),
                source.lastDateTime()
            );
        } else {
            final var reportInfo = reportDomainService.getReport(source.reportId());
            return new OlapRequestGeneralInfo(
                source.userName(),
                source.reportJobId(),
                source.reportId(),
                reportInfo.getName(),
                source.reportJobOwner(),
                source.requestURL(),
                source.requestCount(),
                source.firstDateTime(),
                source.lastDateTime()
            );
        }
    }



    @SuppressWarnings({"unchecked", "java:S108"})
    private Long getJobId(Object entry) {
        var jobId = -1L;
        try {
            var map = (Map<String, Object>) entry;
            jobId = Long.parseLong(map.get("jobId").toString());

        } catch (Exception ignored) {}
        return jobId == -1 ? null : jobId;
    }

    @SuppressWarnings({"unchecked", "java:S108"})
    private Long getReportId(Object entry) {
        var reportId = -1L;
        try {
            var map = (Map<String, Object>) entry;
            reportId = Long.parseLong(map.get("reportId").toString());

        } catch (Exception ignored) {}
        return reportId == -1 ? null : reportId;
    }
}
