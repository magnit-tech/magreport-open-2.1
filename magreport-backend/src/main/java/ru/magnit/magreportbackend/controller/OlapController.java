package ru.magnit.magreportbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import ru.magnit.magreportbackend.dto.inner.olap.OlapUserRequestLog;
import ru.magnit.magreportbackend.dto.request.DatePeriodRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapConfigRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapServiceRegisterInfo;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigAddRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigRequest;
import ru.magnit.magreportbackend.dto.request.olap.ReportOlapConfigSetShareRequest;
import ru.magnit.magreportbackend.dto.request.olap.UsersReceivedMyJobsRequest;
import ru.magnit.magreportbackend.dto.request.reportjob.ReportJobRequest;
import ru.magnit.magreportbackend.dto.response.ResponseBody;
import ru.magnit.magreportbackend.dto.response.ResponseList;
import ru.magnit.magreportbackend.dto.response.olap.ExternalOlapServiceInfo;
import ru.magnit.magreportbackend.dto.response.olap.OlapAvailableConfigurationsResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapFieldItemsResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapInfoCubesResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapRequestGeneralInfo;
import ru.magnit.magreportbackend.dto.response.olap.ReportOlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.TokenResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortInfoResponse;
import ru.magnit.magreportbackend.exception.OlapException;
import ru.magnit.magreportbackend.service.ExternalOlapManager;
import ru.magnit.magreportbackend.service.ExternalOlapService;
import ru.magnit.magreportbackend.service.OlapConfigurationService;
import ru.magnit.magreportbackend.service.OlapLogService;
import ru.magnit.magreportbackend.service.OlapService;
import ru.magnit.magreportbackend.service.ReportJobService;
import ru.magnit.magreportbackend.service.UserService;
import ru.magnit.magreportbackend.service.domain.TokenService;
import ru.magnit.magreportbackend.util.LogHelper;
import ru.magnit.magreportbackend.util.MultipartFileSender;
import ru.magnit.magreportbackend.util.Pair;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Запросы для фронтового движка OLAP")
public class OlapController {

    @Value("${magreport.olap.out-service}")
    private boolean outService;

    @Value("${magreport.olap.max-dop}")
    private int maxDop;

    @Value("${magreport.olap.max-dop-pivot}")
    private int maxDopPivot;

    @Value("${magreport.olap.max-export-time}")
    private int maxExportTime;

    private final ConcurrentHashMap<Thread, LocalDateTime> currentDop = new ConcurrentHashMap<>();

    public static final String OLAP_GET_CUBE_NEW = "/api/v1/olap/get-cube-new";
    public static final String OLAP_GET_FIELD_VALUES = "/api/v1/olap/get-field-values";
    public static final String OLAP_CONFIGURATION_REPORT_ADD = "/api/v1/olap/configuration/report-add";
    public static final String OLAP_CONFIGURATION_REPORT_GET = "/api/v1/olap/configuration/report-get";
    public static final String OLAP_GET_USERS_RECEIVED_MY_JOB = "/api/v1/olap/get-users-received-my-jobs";
    public static final String OLAP_CONFIGURATION_REPORT_SET_DEFAULT = "/api/v1/olap/configuration/set-default";
    public static final String OLAP_CONFIGURATION_DELETE = "/api/v1/olap/configuration/delete";
    public static final String OLAP_CONFIGURATION_REPORT_DELETE = "/api/v1/olap/configuration/report-delete";
    public static final String OLAP_CONFIGURATION_REPORT_SHARED = "/api/v1/olap/configuration/report-share";
    public static final String OLAP_CONFIGURATION_GET_AVAILABLE = "/api/v1/olap/configuration/get-available";
    public static final String OLAP_CONFIGURATION_GET_CURRENT = "/api/v1/olap/configuration/get-current";
    public static final String OLAP_GET_INFO_CUBES = "/api/v1/olap/get-info-cubes";
    public static final String OLAP_GET_LOG_INFO = "/api/v1/olap/get-log-info";
    public static final String OLAP_REGISTER_EXTERNAL_SERVICE = "/api/v1/olap/register-service";
    public static final String OLAP_GET_EXTERNAL_SERVICES = "/api/v1/olap/get-external-services";
    public static final String OLAP_GET_PIVOT_TABLE_EXCEL = "/api/v1/olap/create-excel-pivot-table";
    public static final String OLAP_GET_PIVOT_TABLE_EXCEL_GET = "/api/v1/olap/excel-pivot-table/{pivotToken}";

    public static final String START_ERROR_MASSAGE = "Error executing OLAP request: ";

    private final OlapService olapService;
    private final ExternalOlapService externalOlapService;
    private final OlapConfigurationService olapConfigurationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final OlapLogService olapLogService;
    private final ExternalOlapManager externalOlapManager;
    private final ReportJobService reportJobService;

    private final TokenService tokenService;
    private Semaphore semaphore;


    @Qualifier("OlapRequestExecutor")
    private final ThreadPoolTaskExecutor olapExecutor;

    ConcurrentHashMap<ResponseBodyEmitter, Pair<Long, Future<?>>> emitters = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        semaphore = new Semaphore(maxDop);
    }


    @Operation(summary = "Получение среза OLAP куба с производными полями")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_CUBE_NEW,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyEmitter> getCubeNew(@RequestBody OlapCubeRequestNew request) throws JsonProcessingException {

        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_GET_CUBE_NEW, request, userService.getCurrentUserName()));

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        emitter.onCompletion(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError(throwable -> {
            emitter.completeWithError(throwable);
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> emitters.remove(emitter));


        if (outService) {

            var task = olapExecutor.submit(() -> {
                try {
                    var response = ResponseBody.<OlapCubeResponse>builder()
                            .success(true)
                            .message("")
                            .data(externalOlapService.getCubeNew(request))
                            .build();

                    emitters.remove(emitter);
                    emitter.send(response, APPLICATION_JSON);
                    emitter.complete();
                    log.debug("Processing of cube completed. Job id: " + request.getJobId());
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            });

            emitters.put(emitter, new Pair<>(request.getJobId(),task));

        } else {

            var userId = userService.getCurrentUserId();
            var task = olapExecutor.submit(() -> {
                try {
                        var response = ResponseBody.<OlapCubeResponse>builder()
                                .success(true)
                                .message("")
                                .data(olapService.getCubeNew(request, userId))
                                .build();

                        emitters.remove(emitter);
                        emitter.send(response, APPLICATION_JSON);
                        emitter.complete();
                        log.debug("Processing of cube completed. Job id: " + request.getJobId());

                    } catch (Exception e) {
                        log.error(String.format("Processing of cube completed with error! Job id: %s. Error: %s", request.getJobId(),e.getMessage()),e);
                        emitter.completeWithError(e);
                        emitters.remove(emitter);
                    }
            });

            emitters.put(emitter, new Pair<>(request.getJobId(),task));
        }

        LogHelper.logInfoUserMethodEnd();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);

        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }

    @Operation(summary = "Получение списка уникальных значений поля с учетом фильтра")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_FIELD_VALUES,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<OlapFieldItemsResponse> getFilteredFieldValues(
            @RequestBody
            OlapFieldItemsRequestNew request) throws
            OlapException, InterruptedException, JsonProcessingException {
        ResponseBody<OlapFieldItemsResponse> response;
        LogHelper.logInfoUserMethodStart();

        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_GET_FIELD_VALUES, request, userService.getCurrentUserName()));

        if (outService) {
            response = ResponseBody.<OlapFieldItemsResponse>builder()
                    .success(true)
                    .message("")
                    .data(externalOlapService.getFieldValues(request))
                    .build();
        } else {
            semaphore.acquire();
            try {
                response = ResponseBody.<OlapFieldItemsResponse>builder()
                        .success(true)
                        .message("")
                        .data(olapService.getFieldValues(request))
                        .build();
            } catch (Exception ex) {
                throw new OlapException(START_ERROR_MASSAGE + ex.getMessage(), ex);
            } finally {
                semaphore.release();
            }
        }

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Сохранение и привязка конфигурации к отчету/пользователю/заданию")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_REPORT_ADD,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportOlapConfigResponse> addConfigurationUser(
            @RequestBody
            ReportOlapConfigAddRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_REPORT_ADD, request, userService.getCurrentUserName()));

        var response = ResponseBody.<ReportOlapConfigResponse>builder()
                .success(true)
                .message("")
                .data(olapConfigurationService.addOlapReportConfig(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Получения привязки конфиграции")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_REPORT_GET,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportOlapConfigResponse> getConfigurationUser(
            @RequestBody
            ReportOlapConfigRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_REPORT_GET, request, userService.getCurrentUserName()));

        var response = ResponseBody.<ReportOlapConfigResponse>builder()
                .success(true)
                .message("")
                .data(olapConfigurationService.getOlapReportConfig(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение списка пользователей, с которыми данный пользователь делился заданиями")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_USERS_RECEIVED_MY_JOB,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<UserShortInfoResponse> getUsersReceivedMyJobs(
            @RequestBody
            UsersReceivedMyJobsRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_GET_USERS_RECEIVED_MY_JOB, request, userService.getCurrentUserName()));

        var response = ResponseList.<UserShortInfoResponse>builder()
                .success(true)
                .message("")
                .data(olapConfigurationService.getUsersReceivedMyJobs(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Установка конфигурации по умолчанию")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_REPORT_SET_DEFAULT,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<UserShortInfoResponse> setConfigurationDefault(
            @RequestBody
            ReportOlapConfigRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_REPORT_SET_DEFAULT, request, userService.getCurrentUserName()));

        olapConfigurationService.setDefaultReportConfiguration(request);

        var response = ResponseList.<UserShortInfoResponse>builder()
                .success(true)
                .message("Default configuration set successfully")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Удаление конфигурации")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_DELETE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<OlapConfigResponse> deleteConfiguration(
            @RequestBody
            OlapConfigRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_DELETE, request, userService.getCurrentUserName()));

        olapConfigurationService.deleteOlapConfig(request);
        var response = ResponseBody.<OlapConfigResponse>builder()
                .success(true)
                .message("Configuration delete successfully")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }


    @Operation(summary = "Удаление привязки конфиграции")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_REPORT_DELETE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportOlapConfigResponse> deleteConfigurationUser(
            @RequestBody
            ReportOlapConfigRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_REPORT_DELETE, request, userService.getCurrentUserName()));

        olapConfigurationService.deleteOlapReportConfig(request);

        var response = ResponseBody.<ReportOlapConfigResponse>builder()
                .success(true)
                .message("Report configuration delete successfully")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Управление общим доступом к конфигурации")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_REPORT_SHARED,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportOlapConfigResponse> shareConfigurationUser(
            @RequestBody
            ReportOlapConfigSetShareRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_REPORT_SHARED, request, userService.getCurrentUserName()));
        olapConfigurationService.setSharedStatusReportConfiguration(request);

        var response = ResponseBody.<ReportOlapConfigResponse>builder()
                .success(true)
                .message("Report configuration status shared update successfully")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение списка доступных конфигураций пользователю")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_GET_AVAILABLE,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<OlapAvailableConfigurationsResponse> getConfigurationsUser(
            @RequestBody ReportJobRequest request) throws JsonProcessingException {
        LogHelper.logInfoUserMethodStart();
        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_CONFIGURATION_GET_AVAILABLE, request, userService.getCurrentUserName()));

        var response = ResponseBody.<OlapAvailableConfigurationsResponse>builder()
                .success(true)
                .message("")
                .data(olapConfigurationService.getAvailableConfigurationsForJob(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }


    @Operation(description = "Получение текущей конфигурации")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_CONFIGURATION_GET_CURRENT,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<ReportOlapConfigResponse> getCurrentConfigurationUser(
            @RequestBody ReportJobRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<ReportOlapConfigResponse>builder()
                .success(true)
                .message("")
                .data(olapConfigurationService.getCurrentConfiguration(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Получение информации о кубах")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_INFO_CUBES,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<OlapInfoCubesResponse> getInfoAboutCubes() {

        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<OlapInfoCubesResponse>builder()
                .success(true)
                .message("")
                .data(olapService.getInfoAboutCubes())
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Получение информации из логов запросов к сервису OLAP")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_LOG_INFO,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<OlapRequestGeneralInfo> getGeneralLogInfo(@RequestBody DatePeriodRequest request) {

        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<OlapRequestGeneralInfo>builder()
                .success(true)
                .message("")
                .data(olapLogService.getGeneralLogInfo(request))
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(description = "Регистрация внешнего OLAP сервиса")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_REGISTER_EXTERNAL_SERVICE,
            consumes = APPLICATION_JSON_VALUE)
    public void registerOlapService(@RequestBody OlapServiceRegisterInfo request) {
        LogHelper.logInfoUserMethodStart();

        externalOlapManager.registerExternalService(request);

        LogHelper.logInfoUserMethodEnd();
    }

    @Operation(description = "Получение списка внешних OLAP сервисов")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_EXTERNAL_SERVICES,
            produces = APPLICATION_JSON_VALUE)
    public ResponseList<ExternalOlapServiceInfo> getExternalOlapServices() {

        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<ExternalOlapServiceInfo>builder()
                .success(true)
                .message("")
                .data(externalOlapManager.getExternalServices())
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }


    @Operation(summary = "Получение сводной таблицы в Excel")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = OLAP_GET_PIVOT_TABLE_EXCEL_GET)
    public void getExcelPivotTable(
            @PathVariable
            String pivotToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LogHelper.logInfoUserMethodStart();

        final var value = tokenService.getAssociatedValue(pivotToken);
        final var jobId = value.getL();
        final var userId = value.getR();
        final var fileName = reportJobService.getJob(jobId).getReport().name();

        LogHelper.logInfoUserMethodEnd();

        MultipartFileSender.fromPath(olapService.getExcelPivotPath(jobId, userId), fileName)
                .with(request)
                .with(response)
                .serveResource();


    }

    @Operation(description = "Получение токена сводной таблицы в Excel")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = OLAP_GET_PIVOT_TABLE_EXCEL)
    public ResponseBody<TokenResponse> exportPivotTableExcel(
            @RequestBody OlapExportPivotTableRequest dataRequest) throws JsonProcessingException {

        currentDop.forEach((key,value) -> {

            if (value.isAfter(LocalDateTime.now().plusMinutes(maxExportTime))){
                key.interrupt();
                currentDop.remove(key);
            }

        });

        LogHelper.logInfoUserMethodStart();
        log.debug(String.format("%nCurrent count connect:%s.%nМаx connect count: %s", currentDop, maxDopPivot));

        if (currentDop.size() < maxDopPivot) {
            LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(OLAP_GET_PIVOT_TABLE_EXCEL, dataRequest, userService.getCurrentUserName()));
            currentDop.put(Thread.currentThread(), LocalDateTime.now());
            TokenResponse response;

            try {
                response = olapService.exportPivotTableExcel(dataRequest);
            } catch (Exception ex) {
                currentDop.remove(Thread.currentThread());
                throw new OlapException(ex.getMessage(), ex);
            }

            var dataResponse = ResponseBody.<TokenResponse>builder()
                    .success(true)
                    .message("")
                    .data(response)
                    .build();

            currentDop.remove(Thread.currentThread());
            LogHelper.logInfoUserMethodEnd();

            return dataResponse;
        } else {


            var dataResponse = ResponseBody.<TokenResponse>builder()
                    .success(false)
                    .message("Нет свободных подключений, повторите попытку позднее")
                    .data(null)
                    .build();

            LogHelper.logInfoUserMethodEnd();
            return dataResponse;

        }
    }

    @Scheduled(fixedDelayString = "500", initialDelay = 1000L)
    private void checkEmitters() {
        emitters.forEach((emitter, task) -> {
                    try {
                        emitter.send(" ", APPLICATION_JSON);
                    } catch (Exception ex) {
                        task.getR().cancel(true);
                        emitter.completeWithError(ex);
                        emitters.remove(emitter);
                        log.debug("Processing of cube aborted! Job id: " + task.getL());
                    }
                }
        );
    }
}
