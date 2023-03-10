package ru.magnit.magreportbackend.service;


import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.domain.user.SystemRoles;
import ru.magnit.magreportbackend.dto.inner.RoleView;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFieldData;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldAddRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldCheckNameRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldGetAvailableRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldRequest;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.FilterDefinition;
import ru.magnit.magreportbackend.dto.request.olap.FilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.FilterGroupNew;
import ru.magnit.magreportbackend.dto.request.olap.MetricDefinition;
import ru.magnit.magreportbackend.dto.request.olap.MetricDefinitionNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldTypeResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.ExpressionResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldTypeResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.mapper.derivedfield.FieldExpressionResponseRequestMapper;
import ru.magnit.magreportbackend.service.domain.DerivedFieldDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Setter
@RequiredArgsConstructor
public class DerivedFieldService {
    private final DerivedFieldDomainService domainService;
    private final UserDomainService userDomainService;
    private final ReportDomainService reportDomainService;
    private final FieldExpressionResponseRequestMapper fieldExpressionResponseRequestMapper;
    @Value("${magreport.derived-fields.expression-max-call-depth}")
    private Long maxCallDepth;

    public DerivedFieldResponse getDerivedField(DerivedFieldRequest request) {
        return domainService.getDerivedField(request.getId());
    }

    public void addDerivedField(DerivedFieldAddRequest request) {
        if (!checkFieldName(new DerivedFieldCheckNameRequest(request.getReportId(), request.getIsPublic(), request.getName()), null)) {
            throw new InvalidParametersException("Нарушены правила именования производного поля. Имя общего поля должно быть уникально на уровне отчета, имя приватного поля должно быть уникально на уровне отчет/пользователь.");
        }
        checkPermission(request);
        final var fieldType = inferFieldType(request);
        domainService.addDerivedField(request, fieldType.getFieldType(), userDomainService.getCurrentUser());
    }

    public void deleteDerivedField(DerivedFieldRequest request) {
        domainService.deleteDerivedField(request.getId());
    }

    public void updateDerivedField(DerivedFieldAddRequest request) {
        if (!checkFieldName(new DerivedFieldCheckNameRequest(request.getReportId(), request.getIsPublic(), request.getName()), request.getId())) {
            throw new InvalidParametersException("Нарушены правила именования производного поля. Имя общего поля должно быть уникально на уровне отчета, имя приватного поля должно быть уникально на уровне отчет/пользователь.");
        }
        checkPermission(request);
        final var fieldType = inferFieldType(request);
        domainService.updateDerivedField(request, fieldType.getFieldType(), userDomainService.getCurrentUser());
    }

    public List<ExpressionResponse> getAllExpressions() {
        return domainService.getAllExpressions();
    }

    public Pair<CubeData, OlapCubeRequest> preProcessCube(CubeData sourceCube, OlapCubeRequestNew request) {
        final var derivedFields = getDerivedFields(sourceCube.reportMetaData().id());
        final var fieldTypes = sourceCube.reportMetaData().getVisibleFields()
            .stream()
            .collect(Collectors.toMap(ReportFieldData::id, ReportFieldData::dataType));

        // Получаем набор производных полей, на которые есть прямые ссылки в запросе
        final var reqDerivedFieldSet = request.getAllFields()
            .stream()
            .filter(field -> field.getFieldType() == OlapFieldTypes.DERIVED_FIELD)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        final var requestDerivedFields = new LinkedHashSet<>(reqDerivedFieldSet);

        // Добавляем все поля, от которых зависят производные поля
        var callDepth = maxCallDepth;
        var prevStepFields = new LinkedHashSet<>(reqDerivedFieldSet);
        while (callDepth-- > 0) {
            final var currentStepFields = prevStepFields.stream()
                .map(field -> derivedFields.get(field.getFieldId()))
                .flatMap(field -> field.getUsedDerivedFieldIds().stream())
                .map(fieldId -> new FieldDefinition()
                    .setFieldId(fieldId)
                    .setFieldType(OlapFieldTypes.DERIVED_FIELD))
                .collect(Collectors.toCollection(LinkedHashSet::new));

            if (currentStepFields.isEmpty()) break;
            reqDerivedFieldSet.addAll(currentStepFields);
            prevStepFields = currentStepFields;
        }

        checkCallDepth(prevStepFields, derivedFields);

        var usedDerivedFields = new ArrayList<>(reqDerivedFieldSet);
        Collections.reverse(usedDerivedFields);
        reqDerivedFieldSet.clear();
        reqDerivedFieldSet.addAll(usedDerivedFields);

        final var fieldIndexes = sourceCube.fieldIndexes().entrySet()
            .stream()
            .map(entry -> new Pair<>(new FieldDefinition(entry.getKey(), OlapFieldTypes.REPORT_FIELD), entry.getValue()))
            .collect(Collectors.toMap(Pair::getL, entry -> new Pair<>(entry.getR(), fieldTypes.get(entry.getL().getFieldId()))));

        var fieldCount = fieldIndexes.keySet().size();
        for (final var derivedField : usedDerivedFields) {
            fieldIndexes.put(derivedField, new Pair<>(fieldCount++, null));
        }

        final var processedCube = initResultCube(sourceCube, fieldCount);
        final var fieldExpressions = usedDerivedFields
            .stream()
            .map(field -> derivedFields.get(field.getFieldId()))
            .map(field -> field.getExpression().getType().init(
                    field.getExpression(),
                    new ExpressionCreationContext(fieldIndexes, processedCube, field)
                )
            )
            .toList();

        // Рассчитываем значения всех полей и добавляем в куб
        final int startColumn = processDerivedFields(sourceCube, usedDerivedFields, fieldIndexes, processedCube, fieldExpressions);

        final var resultCube = initResultCube(sourceCube, startColumn + requestDerivedFields.size());
        var fieldNumber = 0;
        final var reportFieldIndex = sourceCube.reportMetaData().getVisibleFields()
            .stream()
            .map(ReportFieldData::id)
            .max(Long::compareTo).orElseThrow() + 1L;

        final var cubeFields = new ArrayList<>(sourceCube.reportMetaData().getVisibleFields());
        final var derivedFieldsColumns = new HashMap<Long, Long>();
        copyDataToResultCube(derivedFields, requestDerivedFields, fieldIndexes, processedCube, startColumn, resultCube, fieldNumber, reportFieldIndex, cubeFields, derivedFieldsColumns);

        final var counter = new AtomicInteger(0);
        final var resultFieldIndexes = cubeFields.stream().collect(Collectors.toMap(ReportFieldData::id, o -> counter.getAndIncrement()));

        final OlapCubeRequest processedRequest = transformOlapRequest(request, derivedFieldsColumns);

        return new Pair<>(new CubeData(
            new ReportData(
                sourceCube.reportMetaData().id(),
                sourceCube.reportMetaData().name(),
                sourceCube.reportMetaData().description(),
                sourceCube.reportMetaData().schemaName(),
                sourceCube.reportMetaData().tableName(),
                cubeFields,
                sourceCube.reportMetaData().filterGroup(),
                sourceCube.reportMetaData().encryptFile()
            ),
            sourceCube.numRows(),
            resultFieldIndexes,
            resultCube
        ), processedRequest);
    }

    private void checkCallDepth(Collection<FieldDefinition> prevStepFields, Map<Long, DerivedFieldResponse> derivedFields) {
        final var depFieldCount = prevStepFields.stream()
            .map(field -> derivedFields.get(field.getFieldId()))
            .mapToLong(field -> field.getUsedDerivedFieldIds().size())
            .sum();
        if (depFieldCount > 0) {
            throw new InvalidExpression("Глубина вложенности ссылок на производные поля превышает установленный максимум: " + maxCallDepth);
        }
    }

    private OlapCubeRequest transformOlapRequest(OlapCubeRequestNew request, HashMap<Long, Long> derivedFieldsColumns) {
        return new OlapCubeRequest()
            .setJobId(request.getJobId())
            .setColumnsInterval(request.getColumnsInterval())
            .setRowsInterval(request.getRowsInterval())
            .setColumnSort(request.getColumnSort())
            .setRowSort(request.getRowSort())
            .setMetricPlacement(request.getMetricPlacement())
            .setColumnFields(columnsFromNew(request, derivedFieldsColumns))
            .setRowFields(rowsFromNew(request, derivedFieldsColumns))
            .setMetrics(fromNew(request.getMetrics(), derivedFieldsColumns))
            .setFilterGroup(request.getFilterGroup() == null ? null : fromNew(request.getFilterGroup(), derivedFieldsColumns))
            .setMetricFilterGroup(request.getMetricFilterGroup());
    }

    @SuppressWarnings("java:S107")
    private void copyDataToResultCube(Map<Long, DerivedFieldResponse> derivedFields, LinkedHashSet<FieldDefinition> reportDerivedFieldSet, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] processedCube, int startColumn, String[][] resultCube, int fieldNumber, long reportFieldIndex, ArrayList<ReportFieldData> cubeFields, HashMap<Long, Long> derivedFieldsColumns) {
        for (final var derivedField : reportDerivedFieldSet) {
            resultCube[startColumn + fieldNumber] = processedCube[fieldIndexes.get(derivedField).getL()];
            cubeFields.add(new ReportFieldData(
                reportFieldIndex + fieldNumber,
                (int) reportFieldIndex + fieldNumber,
                true,
                fieldIndexes.get(derivedField).getR(),
                "",
                derivedFields.get(derivedField.getFieldId()).getName(),
                derivedFields.get(derivedField.getFieldId()).getDescription()));
            derivedFieldsColumns.put(derivedField.getFieldId(), reportFieldIndex + fieldNumber);
            fieldNumber++;
        }
    }

    private int processDerivedFields(CubeData sourceCube, List<FieldDefinition> reqDerivedFields, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] processedCube, List<BaseExpression> fieldExpressions) {
        final var startColumn = sourceCube.data().length;
        for (var rowIndex = 0; rowIndex < sourceCube.numRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < fieldExpressions.size(); columnIndex++) {
                final var expResult = fieldExpressions.get(columnIndex).calculate(rowIndex);
                if (rowIndex == 0) {
                    final var fieldDefinition = reqDerivedFields.get(columnIndex);
                    fieldIndexes.put(fieldDefinition, new Pair<>(fieldIndexes.get(fieldDefinition).getL(), expResult.getR()));
                }
                processedCube[startColumn + columnIndex][rowIndex] = expResult.getL() == null ? null : expResult.getL().intern();
            }
        }
        return startColumn;
    }

    private LinkedHashSet<Long> columnsFromNew(OlapCubeRequestNew request, Map<Long, Long> derivedFieldsColumns) {
        return request.getColumnFields().stream().map(field -> field.getFieldType() == OlapFieldTypes.REPORT_FIELD ? field.getFieldId() : derivedFieldsColumns.get(field.getFieldId())).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LinkedHashSet<Long> rowsFromNew(OlapCubeRequestNew request, Map<Long, Long> derivedFieldsColumns) {
        return request.getRowFields().stream().map(field -> field.getFieldType() == OlapFieldTypes.REPORT_FIELD ? field.getFieldId() : derivedFieldsColumns.get(field.getFieldId())).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<MetricDefinition> fromNew(List<MetricDefinitionNew> metrics, Map<Long, Long> derivedFieldsColumns) {
        return metrics
            .stream()
            .map(metric -> new MetricDefinition(metric.getField().getFieldType() == OlapFieldTypes.REPORT_FIELD ? metric.getField().getFieldId() : derivedFieldsColumns.get(metric.getField().getFieldId()), metric.getAggregationType()))
            .toList();
    }

    private FilterGroup fromNew(FilterGroupNew filterGroup, Map<Long, Long> derivedFieldsColumns) {
        return new FilterGroup(
            filterGroup.getOperationType(),
            filterGroup.isInvertResult(),
            filterGroup.getChildGroups().stream().map(o -> fromNew(o, derivedFieldsColumns)).toList(),
            filterGroup.getFilters().stream().map(filter -> new FilterDefinition(
                filter.getField().getFieldType() == OlapFieldTypes.REPORT_FIELD ? filter.getField().getFieldId() : derivedFieldsColumns.get(filter.getField().getFieldId()),
                filter.getFilterType(),
                filter.isInvertResult(),
                filter.getRounding(),
                filter.isCanRounding(),
                filter.getValues()
            )).toList()
        );
    }

    private String[][] initResultCube(CubeData sourceCube, int fieldCount) {
        final var resultCube = new String[fieldCount][];
        System.arraycopy(sourceCube.data(), 0, resultCube, 0, sourceCube.data().length);
        for (int i = sourceCube.data().length; i < fieldCount; i++) {
            resultCube[i] = new String[sourceCube.numRows()];
        }
        return resultCube;
    }

    private Map<Long, DerivedFieldResponse> getDerivedFields(long reportId) {
        final var derivedFields = domainService.getDerivedFieldsForReport(reportId);
        return derivedFields.stream()
            .collect(Collectors.toMap(DerivedFieldResponse::getId, Function.identity()));
    }

    public List<DerivedFieldResponse> getDerivedFieldsByReport(DerivedFieldGetAvailableRequest request) {
        final var fields = domainService.getDerivedFieldsForReport(request.getReportId());
        final var currentUserId = userDomainService.getCurrentUser().getId();
        return fields.stream()
            .filter(field -> field.getIsPublic() || field.getUserId().equals(currentUserId) || request.getAdditionalFields().contains(field.getId()))
            .toList();
    }

    public DerivedFieldTypeResponse inferFieldType(DerivedFieldAddRequest request) {
        final var reportFields = reportDomainService.getReportFields(request.getReportId());
        final var derivedFields = domainService.getDerivedFieldsForReport(request.getReportId());

        final var fieldIndexes = reportFields.stream()
            .filter(ReportFieldTypeResponse::getVisible)
            .map(entry -> new Pair<>(new FieldDefinition(entry.getId(), OlapFieldTypes.REPORT_FIELD), entry.getType()))
            .collect(Collectors.toMap(Pair::getL, entry -> new Pair<>(0, entry.getR())));
        derivedFields.forEach(field -> fieldIndexes.put(new FieldDefinition(field.getId(), OlapFieldTypes.DERIVED_FIELD), new Pair<>(0, field.getDataType())));

        final var expressionContext = new ExpressionCreationContext(fieldIndexes, null, null);
        final var fieldExpression = fieldExpressionResponseRequestMapper.from(request.getExpression());
        final var expression = fieldExpression.getType().init(fieldExpression, expressionContext);

        return new DerivedFieldTypeResponse(expression.inferType());
    }

    public boolean checkFieldName(DerivedFieldCheckNameRequest request, Long fieldId) {
        final var userId = userDomainService.getCurrentUser().getId();
        final var fieldName = Boolean.TRUE.equals(request.getIsPublic()) ?
            request.getReportId() + "_" + request.getName() :
            request.getReportId() + "_" + userId + "_" + request.getName();

        return !domainService.isFieldExists(request.getReportId(), fieldName, fieldId);
    }

    private void checkPermission(DerivedFieldAddRequest request) {
        if (Boolean.TRUE.equals(request.getIsPublic())) {
            final var userRoes = userDomainService.getCurrentUserRoles(null);
            final var isDeveloper = userRoes.stream().anyMatch(role -> role.getName().equals(SystemRoles.DEVELOPER.name()) || role.getName().equals(SystemRoles.ADMIN.name()));
            final var hasWriteAccess = reportDomainService.isReportAccessible(request.getReportId(), FolderAuthorityEnum.WRITE, userRoes.stream().map(RoleView::getId).toList());
            if (!isDeveloper) {
                throw new InvalidParametersException("Для создания общедоступных производных полей необходимо обладать ролью DEVELOPER.");
            }
            if (!hasWriteAccess) {
                throw new InvalidParametersException("Для создания общедоступных производных полей необходимо иметь доступ на запись в каталог отчета.");
            }
        }
    }
}
