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
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldAddRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldCheckNameRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldGetAvailableRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldRequest;
import ru.magnit.magreportbackend.dto.response.ResponseBody;
import ru.magnit.magreportbackend.dto.response.ResponseList;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldTypeResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.ExpressionResponse;
import ru.magnit.magreportbackend.service.DerivedFieldService;
import ru.magnit.magreportbackend.util.LogHelper;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Управление производными полями отчетов")
public class DerivedFieldController {
    public static final String DERIVED_FIELD_GET = "/api/v1/derived-field/get";
    public static final String DERIVED_FIELD_GET_ALL_BY_REPORT = "/api/v1/derived-field/get-by-report";
    public static final String DERIVED_FIELD_INFER_TYPE = "/api/v1/derived-field/infer-type";
    public static final String DERIVED_FIELD_CHECK_NAME = "/api/v1/derived-field/check_name";
    public static final String DERIVED_FIELD_ADD = "/api/v1/derived-field/add";
    public static final String DERIVED_FIELD_UPDATE = "/api/v1/derived-field/edit";
    public static final String DERIVED_FIELD_DELETE = "/api/v1/derived-field/delete";
    public static final String DERIVED_FIELD_EXPRESSIONS_GET_ALL = "/api/v1/derived-field/expressions/get-all";

    private final DerivedFieldService service;

    @Operation(summary = "Получение настроек производного поля")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_GET,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<DerivedFieldResponse> getDerivedField(
        @RequestBody
        DerivedFieldRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<DerivedFieldResponse>builder()
            .success(true)
            .message("")
            .data(service.getDerivedField(request))
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение списка производных полей отчета")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_GET_ALL_BY_REPORT,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseList<DerivedFieldResponse> getDerivedFieldsByReport(
        @RequestBody
        DerivedFieldGetAvailableRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<DerivedFieldResponse>builder()
            .success(true)
            .message("")
            .data(service.getDerivedFieldsByReport(request))
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Получение справочника выражений для расчета производных полей")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_EXPRESSIONS_GET_ALL,
        produces = APPLICATION_JSON_VALUE)
    public ResponseList<ExpressionResponse> getAllExpressions() {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseList.<ExpressionResponse>builder()
            .success(true)
            .message("")
            .data(service.getAllExpressions())
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Вычисление типа производного поля")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_INFER_TYPE,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<DerivedFieldTypeResponse> inferTypeDerivedField(
        @RequestBody
        DerivedFieldAddRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<DerivedFieldTypeResponse>builder()
            .success(true)
            .message("")
            .data(service.inferFieldType(request))
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Проверка корректности имени производного поля (true - имя валидно, false - нет)")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_CHECK_NAME,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Boolean> derivedFieldCheckName(
        @RequestBody
        DerivedFieldCheckNameRequest request) {
        LogHelper.logInfoUserMethodStart();

        var response = ResponseBody.<Boolean>builder()
            .success(true)
            .message("")
            .data(service.checkFieldName(request))
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Добавление производного поля")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_ADD,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> addDerivedField(
        @RequestBody
        DerivedFieldAddRequest request) {
        LogHelper.logInfoUserMethodStart();

        service.addDerivedField(request);

        var response = ResponseBody.builder()
            .success(true)
            .message("")
            .data(null)
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Удаление производного поля")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_DELETE,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> deleteDerivedField(
        @RequestBody
        DerivedFieldRequest request) {
        LogHelper.logInfoUserMethodStart();

        service.deleteDerivedField(request);

        var response = ResponseBody.builder()
            .success(true)
            .message("")
            .data(null)
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }

    @Operation(summary = "Обновление производного поля")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = DERIVED_FIELD_UPDATE,
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
    public ResponseBody<Object> updateDerivedField(
        @RequestBody
        DerivedFieldAddRequest request) {
        LogHelper.logInfoUserMethodStart();

        service.updateDerivedField(request);

        var response = ResponseBody.builder()
            .success(true)
            .message("")
            .data(null)
            .build();

        LogHelper.logInfoUserMethodEnd();
        return response;
    }
}
