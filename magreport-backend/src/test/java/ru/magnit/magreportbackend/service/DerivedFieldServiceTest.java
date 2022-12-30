package ru.magnit.magreportbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.domain.olap.AggregationType;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFieldData;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldAddRequest;
import ru.magnit.magreportbackend.dto.request.derivedfield.DerivedFieldExpressionAddRequest;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.Interval;
import ru.magnit.magreportbackend.dto.request.olap.MetricDefinitionNew;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldTypeResponse;
import ru.magnit.magreportbackend.mapper.derivedfield.FieldExpressionResponseRequestMapper;
import ru.magnit.magreportbackend.service.domain.DerivedFieldDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DerivedFieldServiceTest {
    @Mock
    private DerivedFieldDomainService domainService;

    @Mock
    private ReportDomainService reportDomainService;

    @Spy
    private FieldExpressionResponseRequestMapper fieldMapper = new FieldExpressionResponseRequestMapper();

    @InjectMocks
    private DerivedFieldService service;

    @Test
    void derivedFieldTest() {
        final var cubeData = getTestCubeData();

        when(domainService.getDerivedFieldsForReport(anyLong())).thenReturn(getDerivedFields());
        service.setMaxCallDepth(5L);

        final var result = service.preProcessCube(cubeData, getOlapRequest());
        assertNotNull(result);
    }

    @Test
    void fieldTypeInferenceTest() {
        when(reportDomainService.getReportFields(anyLong())).thenReturn(List.of(
            new ReportFieldTypeResponse(1L, true, 1, DataTypeEnum.INTEGER),
            new ReportFieldTypeResponse(2L, true, 2, DataTypeEnum.STRING),
            new ReportFieldTypeResponse(3L, true, 3, DataTypeEnum.DOUBLE)
        ));
        when(domainService.getDerivedFieldsForReport(anyLong())).thenReturn(List.of(
            new DerivedFieldResponse().setId(1L).setDataType(DataTypeEnum.INTEGER),
            new DerivedFieldResponse().setId(2L).setDataType(DataTypeEnum.STRING),
            new DerivedFieldResponse().setId(3L).setDataType(DataTypeEnum.DOUBLE)
        ));

        final var result = service.inferFieldType(
            new DerivedFieldAddRequest()
                .setReportId(1L)
                .setExpression(new DerivedFieldExpressionAddRequest(
                        1L,
                        Expressions.ADD,
                        null,
                        null,
                        null,
                        List.of(new DerivedFieldExpressionAddRequest(
                                1L,
                                Expressions.REPORT_FIELD_VALUE,
                                1L,
                                null,
                                null,
                                List.of()
                            ),
                            new DerivedFieldExpressionAddRequest(
                                1L,
                                Expressions.DERIVED_FIELD_VALUE,
                                3L,
                                null,
                                null,
                                List.of()
                            )
                        )
                    )
                )
        );

        assertEquals(DataTypeEnum.DOUBLE, result.getFieldType());
    }

    private OlapCubeRequestNew getOlapRequest() {
        return new OlapCubeRequestNew()
            .setJobId(1L)
            .setColumnFields(new LinkedHashSet<>(List.of(new FieldDefinition(0L, OlapFieldTypes.REPORT_FIELD))))
            .setRowFields(new LinkedHashSet<>(List.of(new FieldDefinition(2L, OlapFieldTypes.REPORT_FIELD))))
            .setMetrics(List.of(
                new MetricDefinitionNew(new FieldDefinition(6L, OlapFieldTypes.REPORT_FIELD), AggregationType.SUM),
                new MetricDefinitionNew(new FieldDefinition(6L, OlapFieldTypes.REPORT_FIELD), AggregationType.MIN),
                new MetricDefinitionNew(new FieldDefinition(6L, OlapFieldTypes.REPORT_FIELD), AggregationType.MAX),
                new MetricDefinitionNew(new FieldDefinition(6L, OlapFieldTypes.REPORT_FIELD), AggregationType.AVG),
                new MetricDefinitionNew(new FieldDefinition(6L, OlapFieldTypes.REPORT_FIELD), AggregationType.COUNT),
                new MetricDefinitionNew(new FieldDefinition(6L, OlapFieldTypes.REPORT_FIELD), AggregationType.COUNT_DISTINCT),
                new MetricDefinitionNew(new FieldDefinition(3L, OlapFieldTypes.DERIVED_FIELD), AggregationType.MAX),
                new MetricDefinitionNew(new FieldDefinition(2L, OlapFieldTypes.DERIVED_FIELD), AggregationType.SUM)))
            .setMetricFilterGroup(new MetricFilterGroup())
            .setColumnsInterval(new Interval(0, 10))
            .setRowsInterval(new Interval(0, 10));
    }

    private String[][] getDataArray() {
        return new String[][]{
            new String[]{null, null, null, null, null, null, null},
            new String[]{"2021-11-03", "Russia", "Store 1", "Product type 1", "Product 1", "10", "5.5"},
            new String[]{"2021-11-03", "Russia", "Store 1", "Product type 2", "Product 2", "15", "4"},
            new String[]{"2021-11-03", "Russia", "Store 2", "Product type 1", "Product 1", "10", "5.5"},
            new String[]{"2021-11-03", "Germany", "Store 3", "Product type 2", "Product 2", "10", "5.5"},
            new String[]{"2021-11-03", "Germany", "Store 3", "Product type 3", "Product 3", "10", "5.5"},
            new String[]{"2021-11-03", "Germany", "Store 3", "Product type 4", "Product 4", "10", "5.5"},
            new String[]{"2021-11-03", "Turkey", "Store 4", "Product type 3", "Product 5", "10", "5.5"},
            new String[]{"2021-11-03", "Turkey", "Store 5", "Product type 3", "Product 6", "10", "5.5"},
            new String[]{"2021-11-03", "Turkey", "Store 4", "Product type 5", "Product 7", "10", "5.5"},
            new String[]{"2021-11-03", "Ukraine", "Store 6", "Product type 1", "Product 1", "10", "5.5"},
            new String[]{"2021-11-03", "Ukraine", "Store 7", "Product type 2", "Product 2", "10", "5.5"},
            new String[]{"2021-11-03", "Ukraine", "Store 8", "Product type 2", "Product 2", "10", "5.5"},
        };
    }

    private List<ReportFieldData> getReportFields() {
        return List.of(
            new ReportFieldData(1, 1, true, DataTypeEnum.DATE, "OPER_DATE", "Date of operation", ""),
            new ReportFieldData(2, 2, false, DataTypeEnum.STRING, "HIDDEN_FIELD", "Hidden field", ""),
            new ReportFieldData(3, 3, true, DataTypeEnum.STRING, "REGION_NAME", "Region name", ""),
            new ReportFieldData(4, 4, true, DataTypeEnum.STRING, "STORE_NAME", "Store name", ""),
            new ReportFieldData(5, 5, true, DataTypeEnum.STRING, "PRODUCT_TYPE", "Product type", ""),
            new ReportFieldData(6, 6, true, DataTypeEnum.DATE, "PRODUCT_NAME", "Product name", ""),
            new ReportFieldData(7, 7, true, DataTypeEnum.INTEGER, "SALE_QTY", "Quantity of sold products", ""),
            new ReportFieldData(8, 8, true, DataTypeEnum.DOUBLE, "SALE_SUM", "Sum of sold products", "")
        );
    }

    private ReportData getTestReportData() {
        return new ReportData(
            1,
            "TestReport",
            "TestReport",
            "TEST",
            "CUBE_TABLE",
            getReportFields(),
            null,
            true
        );
    }

    private CubeData getTestCubeData() {
        final var dataArray = transposeArray(getDataArray());
        final var counter = new AtomicInteger(0);

        return new CubeData(
            getTestReportData(),
            dataArray[0].length,
            getReportFields().stream().filter(ReportFieldData::visible).collect(Collectors.toMap(ReportFieldData::id, o -> counter.getAndIncrement())),
            dataArray
        );
    }

    private String[][] transposeArray(String[][] sourceArray) {
        final var result = new String[sourceArray[0].length][sourceArray.length];

        for (int i = 0; i < sourceArray.length; i++) {
            for (int j = 0; j < sourceArray[0].length; j++) {
                result[j][i] = sourceArray[i][j];
            }
        }

        return result;
    }

    private List<DerivedFieldResponse> getDerivedFields() {
        return List.of(
            new DerivedFieldResponse(
                1L,
                1L,
                DataTypeEnum.DOUBLE,
                "Объем продаж",
                "Объем продаж",
                1L,
                "superuser",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new FieldExpressionResponse(
                    Expressions.ADD,
                    null,
                    null,
                    null,
                    List.of(
                        new FieldExpressionResponse(
                            Expressions.NVL,
                            null,
                            null,
                            null,
                            List.of(
                                new FieldExpressionResponse(
                                    Expressions.REPORT_FIELD_VALUE,
                                    7L,
                                    null,
                                    null,
                                    Collections.emptyList()
                                ),
                                new FieldExpressionResponse(
                                    Expressions.CONSTANT_VALUE,
                                    null,
                                    "0",
                                    DataTypeEnum.INTEGER,
                                    Collections.emptyList()
                                )
                            )
                        ),
                        new FieldExpressionResponse(
                            Expressions.NVL,
                            null,
                            null,
                            null,
                            List.of(
                                new FieldExpressionResponse(
                                    Expressions.REPORT_FIELD_VALUE,
                                    8L,
                                    null,
                                    null,
                                    Collections.emptyList()
                                ),
                                new FieldExpressionResponse(
                                    Expressions.CONSTANT_VALUE,
                                    null,
                                    "0",
                                    DataTypeEnum.DOUBLE,
                                    Collections.emptyList()
                                )
                            )
                        )
                    )
                ),
                ""
            ),
            new DerivedFieldResponse(
                2L,
                1L,
                DataTypeEnum.DOUBLE,
                "Поле 2",
                "Поле 2",
                1L,
                "superuser",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new FieldExpressionResponse(
                    Expressions.ADD,
                    null,
                    null,
                    null,
                    List.of(
                        new FieldExpressionResponse(
                            Expressions.CONSTANT_VALUE,
                            null,
                            "1",
                            DataTypeEnum.INTEGER,
                            Collections.emptyList()
                        ),
                        new FieldExpressionResponse(
                            Expressions.DERIVED_FIELD_VALUE,
                            1L,
                            null,
                            null,
                            Collections.emptyList()
                        )
                    )
                ),
                ""
            ),
            new DerivedFieldResponse(
                3L,
                1L,
                DataTypeEnum.INTEGER,
                "Номер года",
                "Расчет года",
                1L,
                "superuser",
                LocalDateTime.now(),
                LocalDateTime.now(),
                new FieldExpressionResponse(
                    Expressions.LEFT_SUBSTR,
                    null,
                    null,
                    null,
                    List.of(
                        new FieldExpressionResponse(
                            Expressions.NVL,
                            null,
                            null,
                            null,
                            List.of(
                                new FieldExpressionResponse(
                                    Expressions.REPORT_FIELD_VALUE,
                                    1L,
                                    null,
                                    null,
                                    Collections.emptyList()
                                ),
                                new FieldExpressionResponse(
                                    Expressions.CONSTANT_VALUE,
                                    null,
                                    "null",
                                    DataTypeEnum.STRING,
                                    Collections.emptyList()
                                )
                            )
                        ),
                        new FieldExpressionResponse(
                            Expressions.CONSTANT_VALUE,
                            null,
                            "4",
                            DataTypeEnum.INTEGER,
                            Collections.emptyList()
                        )
                    )
                ),
                ""
            )
        );
    }
}
