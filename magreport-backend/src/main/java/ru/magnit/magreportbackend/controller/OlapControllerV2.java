package ru.magnit.magreportbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.magnit.magreportbackend.dto.inner.olap.OlapUserRequestLog;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestV2;
import ru.magnit.magreportbackend.dto.response.ResponseBody;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponseV2;
import ru.magnit.magreportbackend.exception.OlapException;
import ru.magnit.magreportbackend.service.OlapServiceV2;
import ru.magnit.magreportbackend.service.UserService;
import ru.magnit.magreportbackend.util.LogHelper;

import javax.annotation.PostConstruct;
import java.util.concurrent.Semaphore;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Запросы для фронтового движка OLAP 2.0")
@SuppressWarnings("Duplicates")
public class OlapControllerV2 {
    @Value("${magreport.olap.out-service}")
    private boolean outService;

    @Value("${magreport.olap.max-dop}")
    private int maxDop;
    private Semaphore semaphore;

    private final OlapServiceV2 olapService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public static final String GET_OLAP_CUBE_V2 = "/api/v2/olap/get-cube";

    @PostConstruct
    private void init() {
        semaphore = new Semaphore(maxDop);
    }

    @Operation(summary = "Получение среза OLAP куба с производными полями и метриками")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = GET_OLAP_CUBE_V2,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<OlapCubeResponseV2> getCube(
            @RequestBody
            OlapCubeRequestV2 request) throws JsonProcessingException, InterruptedException {
        ResponseBody<OlapCubeResponseV2> response;
        LogHelper.logInfoUserMethodStart();

        LogHelper.logInfoOlapUserRequest(objectMapper, new OlapUserRequestLog(GET_OLAP_CUBE_V2, request, userService.getCurrentUserName()));

        if (outService) {
            response = ResponseBody.<OlapCubeResponseV2>builder()
                    .success(false)
                    .message("No external OLAP services available.")
                    .data(null)
                    .build();

        } else {
            semaphore.acquire();
            try {
                response = ResponseBody.<OlapCubeResponseV2>builder()
                        .success(true)
                        .message("")
                        .data(olapService.getCube(request))
                        .build();
            } catch (Exception ex) {
                throw new OlapException("Error executing OLAP request: " + ex.getMessage(), ex);
            } finally {
                semaphore.release();
            }
        }

        LogHelper.logInfoUserMethodEnd();
        return response;
    }
}
