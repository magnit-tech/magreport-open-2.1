package ru.magnit.magreportbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import ru.magnit.magreportbackend.dto.request.reportjob.*;
import ru.magnit.magreportbackend.dto.response.ResponseBody;
import ru.magnit.magreportbackend.dto.response.ResponseList;
import ru.magnit.magreportbackend.dto.response.reportjob.*;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.service.ReportJobService;
import ru.magnit.magreportbackend.service.domain.TokenService;
import ru.magnit.magreportbackend.util.LogHelper;
import ru.magnit.magreportbackend.util.MultipartFileSender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Управление заданиями на формирование отчетов")
public class ReportJobController {
    private final ReportJobService service;
    private final TokenService tokenService;

    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    public static final String REPORT_JOB_GET_REPORT_PAGE = "/api/v1/report-job/get-data-page";
    public static final String REPORT_JOB_REPORT_STREAM = "/api/v1/report-job/{jobId}/stream";
    public static final String REPORT_JOB_GET_EXCEL_REPORT = "/api/v1/report-job/get-excel-report";
    public static final String REPORT_JOB_GET_EXCEL_REPORT_GET = "/api/v1/report-job/excel-report/{reportToken}";
    public static final String REPORT_JOB_ADD = "/api/v1/report-job/add";
    public static final String REPORT_JOB_GET = "/api/v1/report-job/get";
    public static final String REPORT_JOB_DELETE = "/api/v1/report-job/delete";
    public static final String REPORT_JOB_DELETE_ALL = "/api/v1/report-job/clear";
    public static final String REPORT_JOB_GET_ALL_MY_JOBS = "/api/v1/report-job/get-all-my-jobs";
    public static final String REPORT_JOB_GET_ALL_JOBS = "/api/v1/report-job/get-all-jobs";
    public static final String REPORT_JOB_CANCEL = "/api/v1/report-job/cancel";
    public static final String REPORT_JOB_QUERY = "/api/v1/report-job/get-sql-query";
    public static final String REPORT_JOB_GET_METADATA = "/api/v1/report-job/get-metadata";
    public static final String REPORT_JOB_SHARE = "/api/v1/report-job/share";
    public static final String REPORT_JOB_GET_USERS_JOB = "/api/v1/report-job/get-users-job";
    public static final String REPORT_JOB_COMMENT = "/api/v1/report-job/add-comment";
    public static final String REPORT_JOB_GET_HISTORY = "/api/v1/report-job/get-history";
    public static final String REPORT_JOB_GET_ALL_REPORTS = "/api/v1/report-job/get-all-reports";

    @Operation(summary = "Получение страницы отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_REPORT_PAGE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportPageResponse> getReportPage(
            @RequestBody
                    ReportPageRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportPageResponse>builder()
                .success(true)
                .message("")
                .data(service.getReportPage(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Стриминг данных отчета клиенту")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = REPORT_JOB_REPORT_STREAM,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyEmitter> streamReportData(@PathVariable Long jobId) {
        LogHelper.logInfoUserMethodStart();

        final var emitter = new ResponseBodyEmitter();
        nonBlockingService.execute(() -> {
            try {
                service.streamReport(emitter, jobId);
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        LogHelper.logInfoUserMethodEnd();
        final var contentType = new HttpHeaders();
        contentType.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(emitter, contentType, HttpStatus.OK);
    }

    @Operation(summary = "Получение отчета в формате Excel")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_EXCEL_REPORT,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<TokenResponse> getExcelReportPost(
            @RequestBody
                    ExcelReportRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<TokenResponse>builder()
                .success(true)
                .message("")
                .data(service.createExcelReport(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }


    @SuppressWarnings("Duplicates")
    @Operation(summary = "Получение отчета в формате Excel")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = REPORT_JOB_GET_EXCEL_REPORT_GET)
    public void getExcelReportGet(
            @PathVariable
                    String reportToken, HttpServletRequest request, HttpServletResponse response ) throws Exception {
        LogHelper.logInfoUserMethodStart();

        final var value = tokenService.getAssociatedValue(reportToken);
        final var jobId = value.getL();
        final var templateId = value.getR();
        final var fileName = service.getJob(jobId).getReport().name();

        LogHelper.logInfoUserMethodEnd();

        MultipartFileSender.fromPath(service.getExcelReportPath(jobId,templateId), fileName)
                .with(request)
                .with(response)
                .serveResource();
    }

    @Operation(summary = "Добавление задания на выполнение отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_ADD,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportJobResponse> addJob(
            @RequestBody
                    ReportJobAddRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportJobResponse>builder()
                .success(true)
                .message("")
                .data(service.addJob(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение информации о задании на выполнение отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportJobResponse> getJob(
            @RequestBody
                    ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportJobResponse>builder()
                .success(true)
                .message("")
                .data(service.getJob(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Удаление задания на выполнение отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_DELETE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> deleteJob(
            @RequestBody
                    ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        service.deleteJob(request);

        var response = ResponseBody.builder()
                .success(true)
                .message("Job with ID:" + request.getJobId() + " successfully deleted.")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Удаление задания на выполнение отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_DELETE_ALL,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> deleteAllJobs() {
        LogHelper.logInfoUserMethodStart();

        service.deleteAllJobs();

        var response = ResponseBody.builder()
                .success(true)
                .message("All Jobs successfully deleted.")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение информации о заданиях на выполнение отчетов текущего пользователя")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_ALL_MY_JOBS,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<ReportJobResponse> getMyJobs(@RequestBody(required = false) ReportJobHistoryRequestFilter filter) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<ReportJobResponse>builder()
                .success(true)
                .message("")
                .data(service.getMyJobs(filter))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение информации об отчетах в заданиях пользователей")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_ALL_REPORTS,
        produces = APPLICATION_JSON_VALUE)
    public ResponseList<ScheduledReportResponse> getAllScheduledReports() {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<ScheduledReportResponse>builder()
            .success(true)
            .message("")
            .data(service.getAllScheduledReports())
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение информации о всех заданиях на выполнение отчетов")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_ALL_JOBS,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<ReportJobResponse> getAllJobs(@RequestBody(required = false) ReportJobHistoryRequestFilter filter) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<ReportJobResponse>builder()
                .success(true)
                .message("")
                .data(service.getAllJobs(filter))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Отмена задания на выполнение отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_CANCEL,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportJobResponse> cancelJob(
            @RequestBody
                    ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportJobResponse>builder()
                .success(true)
                .message("")
                .data(service.cancelJob(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение SQL-запроса отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_QUERY,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportSqlQueryResponse> getQuery(
            @RequestBody
                    ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportSqlQueryResponse>builder()
                .success(true)
                .message("")
                .data(service.getSqlQuery(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение метаданных задания")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_METADATA,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportJobMetadataResponse> getMetaData(
        @RequestBody
            ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportJobMetadataResponse>builder()
            .success(true)
            .message("")
            .data(service.getMetaData(request))
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение метаданных задания")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_HISTORY,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportJobHistoryResponse> getHistory(
        @RequestBody
        ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportJobHistoryResponse>builder()
            .success(true)
            .message("")
            .data(service.getHistory(request))
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Поделится заданием со списком пользователей")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_SHARE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> shareJob(
            @RequestBody
            ReportJobShareRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.builder()
                .success(true)
                .message(service.shareJob(request))
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Cписок пользователей имеющих доступ к заданию")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_GET_USERS_JOB,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<UserResponse> getUsersForShareJob(
            @RequestBody
            ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();
        service.getUsersJob(request);
        var response = ResponseList.<UserResponse>builder()
                .success(true)
                .message("")
                .data(service.getUsersJob(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }


    @Operation(description = "Добавление комментария к заданию")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = REPORT_JOB_COMMENT,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> addReportJobComment(
            @RequestBody
            ReportJobCommentRequest request) {
        LogHelper.logInfoUserMethodStart();

        service.addReportJobComment(request);

        var response = ResponseBody.builder()
                .success(true)
                .message("Comment added succeeded")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }
}
