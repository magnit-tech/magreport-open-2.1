package ru.magnit.magreportbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.magnit.magreportbackend.dto.request.event.EventPivotRegisterRequest;
import ru.magnit.magreportbackend.dto.request.event.EventRegisterRequest;
import ru.magnit.magreportbackend.dto.response.ResponseBody;
import ru.magnit.magreportbackend.service.EventService;
import ru.magnit.magreportbackend.util.LogHelper;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Управление событиями")
public class EventController {

    private final EventService eventService;

    public static final String EVENT_REGISTER = "/api/v1/event/register";
    public static final String EVENT_PIVOT_REGISTER = "/api/v1/event/pivot-register";

    @Operation(summary = "Регистрация события")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = EVENT_REGISTER,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> eventRegister(
            @RequestBody EventRegisterRequest data, HttpServletRequest request) {

        LogHelper.logInfoUserMethodStart();

        eventService.eventRegister(data, request.getRemoteAddr());

        var response = ResponseBody.builder()
                .success(true)
                .message("")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Регистрация события сводной")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = EVENT_PIVOT_REGISTER,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> eventPivotRegister(
            @RequestBody EventPivotRegisterRequest data) {

        LogHelper.logInfoUserMethodStart();

        eventService.eventPivotRegister(data);

        var response = ResponseBody.builder()
                .success(true)
                .message("")
                .data(null)
                .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

}
