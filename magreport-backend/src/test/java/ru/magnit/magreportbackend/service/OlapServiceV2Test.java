package ru.magnit.magreportbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.domain.olap.AggregationType;
import ru.magnit.magreportbackend.domain.olap.FilterType;
import ru.magnit.magreportbackend.domain.olap.MetricFilterType;
import ru.magnit.magreportbackend.domain.olap.SortingOrder;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFieldData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobData;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.FilterDefinitionNew;
import ru.magnit.magreportbackend.dto.request.olap.FilterGroupNew;
import ru.magnit.magreportbackend.dto.request.olap.Interval;
import ru.magnit.magreportbackend.dto.request.olap.MetricDefinitionNew;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterDefinition;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestV2;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.request.olap.SortingParams;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapConfigResponse;
import ru.magnit.magreportbackend.dto.response.olap.ReportOlapConfigResponse;
import ru.magnit.magreportbackend.mapper.olap.OlapCubeRequestMapper;
import ru.magnit.magreportbackend.mapper.olap.OlapFieldItemsRequestMerger;
import ru.magnit.magreportbackend.service.domain.ExcelReportDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapConfigurationDomainService;
import ru.magnit.magreportbackend.service.domain.OlapDomainService;
import ru.magnit.magreportbackend.service.domain.OlapUserChoiceDomainService;
import ru.magnit.magreportbackend.service.domain.TokenService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.util.Triple;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes.REPORT_FIELD;

@ExtendWith(MockitoExtension.class)
class OlapServiceV2Test {

    @InjectMocks
    private OlapServiceV2 service;
    @Mock
    private OlapDomainService domainService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JobDomainService jobDomainService;
    @Mock
    private ExcelReportDomainService excelReportDomainService;

    @Mock
    private TokenService tokenService;
    @Mock
    private OlapConfigurationDomainService olapConfigurationDomainService;

    @Mock
    private DerivedFieldService derivedFieldService;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private OlapUserChoiceDomainService olapUserChoiceDomainService;
    @Mock
    private OlapCubeRequestMapper olapCubeRequestMapper;
    @Mock
    private OlapFieldItemsRequestMerger olapFieldItemsRequestMerger;



    @Test
    void getCube() {

        when(jobDomainService.getJobData(any())).thenReturn(getReportJobData());
        when(domainService.getCubeData(any())).thenReturn(getTestCubeData());
        when(domainService.filterCubeData(any(), any())).thenReturn(getTrueStatusRows());
        when(domainService.filterMetricResult(any(), any(), any())).thenReturn(getTrueStatusMetricValue());
        when(userDomainService.getCurrentUser()).thenReturn(new UserView());

        final var result1 = service.getCube(getOlapRequest(REPORT_FIELD));
        assertNotNull(result1);
        assertEquals("2021-11-03", result1.getColumnValues().get(1).get(0));
        assertEquals(5, result1.getRowValues().size());
        assertEquals(2, result1.getColumnValues().size());
        assertEquals(6, result1.getMetricValues().size());
        assertEquals("30", result1.getMetricValues().get(0).getValues()[1][1]);
        assertEquals("10", result1.getMetricValues().get(1).getValues()[1][1]);
        assertEquals("10", result1.getMetricValues().get(2).getValues()[1][1]);
        assertEquals("10", result1.getMetricValues().get(3).getValues()[1][1]);
        assertEquals("3", result1.getMetricValues().get(4).getValues()[1][1]);
        assertEquals("1", result1.getMetricValues().get(5).getValues()[1][1]);

        verify(jobDomainService).getJobData(any());
        verify(jobDomainService).checkAccessForJob(any());
        verify(jobDomainService).updateJobStats(any(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(domainService).getCubeData(any());
        verify(domainService).filterCubeData(any(), any());
        verify(domainService).filterMetricResult(any(), any(), any());
        verifyNoMoreInteractions(jobDomainService, domainService, derivedFieldService, tokenService, excelReportDomainService);
    }

    @Test
    void getInfoAboutCubes() {
        when(domainService.getInfoAboutCubes()).thenReturn(Collections.emptyList());

        var result = service.getInfoAboutCubes();

        assertTrue(result.isEmpty());

        verify(domainService).getInfoAboutCubes();
        verifyNoMoreInteractions(domainService);
    }


    private OlapCubeRequestV2 getOlapRequest(OlapFieldTypes type) {
        return new OlapCubeRequestV2()
                .setJobId(1L)
                .setColumnFields(new LinkedHashSet<>(Collections.singleton(new FieldDefinition(0L, REPORT_FIELD))))
                .setRowFields(new LinkedHashSet<>(Collections.singleton(new FieldDefinition(2L, type))))
                .setMetrics(List.of(
                        new FieldExpressionResponse().setType(Expressions.SUM).setFieldType(REPORT_FIELD).setReferenceId(6L),
                        new FieldExpressionResponse().setType(Expressions.MIN).setFieldType(REPORT_FIELD).setReferenceId(6L),
                        new FieldExpressionResponse().setType(Expressions.MAX).setFieldType(REPORT_FIELD).setReferenceId(6L),
                        new FieldExpressionResponse().setType(Expressions.AVG).setFieldType(REPORT_FIELD).setReferenceId(6L),
                        new FieldExpressionResponse().setType(Expressions.COUNT).setFieldType(REPORT_FIELD).setReferenceId(6L),
                        new FieldExpressionResponse().setType(Expressions.COUNT_DISTINCT).setFieldType(REPORT_FIELD).setReferenceId(6L)
               ))
                .setColumnsInterval(new Interval(0, 10))
                .setRowsInterval(new Interval(0, 10))
                .setMetricFilterGroup(new MetricFilterGroup()
                        .setChildGroups(Collections.emptyList())
                        .setInvertResult(true)
                        .setOperationType(BinaryBooleanOperations.OR)
                        .setFilters(Collections.singletonList(
                                new MetricFilterDefinition()
                                        .setMetricId(0L)
                                        .setFilterType(MetricFilterType.EMPTY)
                                        .setValues(Collections.emptyList())
                                        .setRounding(0)
                                        .setInvertResult(true))))
                .setFilterGroup(new FilterGroupNew()
                        .setOperationType(BinaryBooleanOperations.OR)
                        .setInvertResult(false)
                        .setChildGroups(Collections.emptyList())
                        .setFilters(
                                Collections.singletonList(
                                        new FilterDefinitionNew()
                                                .setField(new FieldDefinition(0L, REPORT_FIELD))
                                                .setInvertResult(true)
                                                .setCanRounding(false)
                                                .setValues(Collections.emptyList())
                                                .setFilterType(FilterType.EQUALS)
                                )))
                .setColumnSort(
                        Collections.singletonList(
                                new SortingParams()
                                        .setMetricId((short) 0)
                                        .setOrder(SortingOrder.Descending)
                                        .setTuple(Collections.emptyList())

                        ))
                .setRowSort(
                        Collections.singletonList(
                                new SortingParams()
                                        .setMetricId((short) 0)
                                        .setOrder(SortingOrder.Descending)
                                        .setTuple(Collections.emptyList())

                        ));
    }

    private CubeData getTestCubeData() {
        final var dataArray = transposeArray(getDataArray());

        return new CubeData(
                getTestReportData(),
                dataArray[0].length,
                Map.ofEntries(
                        Map.entry(0L, 0),
                        Map.entry(2L, 1),
                        Map.entry(3L, 2),
                        Map.entry(4L, 3),
                        Map.entry(5L, 4),
                        Map.entry(6L, 5),
                        Map.entry(7L, 6)),
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
                new ReportFieldData(0, 1, true, DataTypeEnum.DATE, "OPER_DATE", "Date of operation", ""),
                new ReportFieldData(1, 2, false, DataTypeEnum.STRING, "HIDDEN_FIELD", "Hidden field", ""),
                new ReportFieldData(2, 3, true, DataTypeEnum.STRING, "REGION_NAME", "Region name", ""),
                new ReportFieldData(3, 4, true, DataTypeEnum.STRING, "STORE_NAME", "Store name", ""),
                new ReportFieldData(4, 5, true, DataTypeEnum.STRING, "PRODUCT_TYPE", "Product type", ""),
                new ReportFieldData(5, 6, true, DataTypeEnum.DATE, "PRODUCT_NAME", "Product name", ""),
                new ReportFieldData(6, 7, true, DataTypeEnum.INTEGER, "SALE_QTY", "Quantity of sold products", ""),
                new ReportFieldData(7, 8, true, DataTypeEnum.DOUBLE, "SALE_SUM", "Sum of sold products", "")
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

    private ReportJobData getTestJobData() {
        return new ReportJobData(
                1,
                1,
                1,
                1,
                1,
                "TestUser",
                1,
                1,
                10,
                true,
                null,
                getTestReportData(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    private OlapFieldItemsRequest getOlapFieldItemsRequest() {
        return new OlapFieldItemsRequest()
                .setJobId(1L)
                .setFieldId(2L)
                .setFrom(0L)
                .setCount(10L);
    }

    private OlapFieldItemsRequestNew getOlapFieldItemsRequestNew() {
        return new OlapFieldItemsRequestNew()
                .setJobId(1L)
                .setFieldId(2L)
                .setFrom(0L)
                .setCount(10L);
    }

    private boolean[] getTrueStatusRows() {
        var result = new boolean[getDataArray().length];
        Arrays.fill(result, true);
        return result;
    }

    private boolean[] getFalseStatusRows() {
        return new boolean[getDataArray().length];
    }


    private Triple<boolean[][], boolean[], boolean[]> getTrueStatusMetricValue() {

        var res1 = new boolean[2][5];
        var res2 = new boolean[2];
        var res3 = new boolean[5];

        Arrays.fill(res2, false);
        Arrays.fill(res3, false);

        return new Triple<>(res1, res2, res3);
    }

    private boolean[] getMixedStatusRows() {
        var result = new boolean[getDataArray().length];

        for (int i = 0; i < result.length; i++)
            if (i < result.length / 2)
                result[i] = true;

        return result;
    }

    private OlapExportPivotTableRequest getOlapExportPivotTableRequest() {
        return new OlapExportPivotTableRequest()
                .setCubeRequest(getOlapCubeRequestNew(REPORT_FIELD))
                .setStylePivotTable(true)
                .setConfiguration(1L);
    }

    private ReportJobData getReportJobData() {
        return new ReportJobData(
                1l,
                1L,
                1L,
                1L,
                1L,
                null,
                1L,
                1L,
                1L,
                true,
                null,
                new ReportData(1L, null, null, null, null, null, null, true),
                null,
                null);
    }

    private ReportOlapConfigResponse getReportOlapConfigResponse() {
        return new ReportOlapConfigResponse()
                .setOlapConfig(
                        new OlapConfigResponse()
                                .setData("!"));
    }

    private OlapCubeRequestNew getOlapCubeRequestNew(OlapFieldTypes type) {

        return new OlapCubeRequestNew()
                .setJobId(1L)
                .setColumnFields(new LinkedHashSet<>(Collections.singleton(new FieldDefinition(0L, REPORT_FIELD))))
                .setRowFields(new LinkedHashSet<>(Collections.singleton(new FieldDefinition(2L, type))))
                .setMetrics(List.of(
                        new MetricDefinitionNew(new FieldDefinition(6L, REPORT_FIELD), AggregationType.SUM),
                        new MetricDefinitionNew(new FieldDefinition(6L, REPORT_FIELD), AggregationType.MIN),
                        new MetricDefinitionNew(new FieldDefinition(6L, REPORT_FIELD), AggregationType.MAX),
                        new MetricDefinitionNew(new FieldDefinition(6L, REPORT_FIELD), AggregationType.AVG),
                        new MetricDefinitionNew(new FieldDefinition(6L, REPORT_FIELD), AggregationType.COUNT),
                        new MetricDefinitionNew(new FieldDefinition(6L, REPORT_FIELD), AggregationType.COUNT_DISTINCT)))
                .setColumnsInterval(new Interval(0, 10))
                .setRowsInterval(new Interval(0, 10))
                .setMetricFilterGroup(new MetricFilterGroup()
                        .setChildGroups(Collections.emptyList())
                        .setInvertResult(true)
                        .setOperationType(BinaryBooleanOperations.OR)
                        .setFilters(Collections.singletonList(
                                new MetricFilterDefinition()
                                        .setMetricId(0L)
                                        .setFilterType(MetricFilterType.EMPTY)
                                        .setValues(Collections.emptyList())
                                        .setRounding(0)
                                        .setInvertResult(true))))
                .setFilterGroup(new FilterGroupNew()
                        .setOperationType(BinaryBooleanOperations.OR)
                        .setInvertResult(false)
                        .setChildGroups(Collections.emptyList())
                        .setFilters(
                                Collections.singletonList(
                                        new FilterDefinitionNew()
                                                .setField(new FieldDefinition(0L, REPORT_FIELD))
                                                .setInvertResult(true)
                                                .setCanRounding(false)
                                                .setValues(Collections.emptyList())
                                                .setFilterType(FilterType.EQUALS)
                                )))
                .setColumnSort(
                        Collections.singletonList(
                                new SortingParams()
                                        .setMetricId((short) 0)
                                        .setOrder(SortingOrder.Descending)
                                        .setTuple(Collections.emptyList())

                        ))
                .setRowSort(
                        Collections.singletonList(
                                new SortingParams()
                                        .setMetricId((short) 0)
                                        .setOrder(SortingOrder.Descending)
                                        .setTuple(Collections.emptyList())

                        ));

    }
}
