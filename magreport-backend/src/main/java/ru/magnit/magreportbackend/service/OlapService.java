package ru.magnit.magreportbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.olap.AggregationType;
import ru.magnit.magreportbackend.domain.olap.SortDirection;
import ru.magnit.magreportbackend.domain.olap.SortingOrder;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.olap.MeasureData;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.inner.olap.Sorting;
import ru.magnit.magreportbackend.dto.request.olap.Interval;
import ru.magnit.magreportbackend.dto.request.olap.MetricDefinition;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequest;
import ru.magnit.magreportbackend.dto.request.olap.SortingParams;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapFieldItemsResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapInfoCubesResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapMetricResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapMetricResponse2;
import ru.magnit.magreportbackend.exception.OlapMaxDataVolumeExceeded;
import ru.magnit.magreportbackend.metrics_function.MetricsFunction;
import ru.magnit.magreportbackend.service.domain.ExcelReportDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapConfigurationDomainService;
import ru.magnit.magreportbackend.service.domain.OlapDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.util.Extensions;
import ru.magnit.magreportbackend.util.Pair;
import ru.magnit.magreportbackend.util.Triple;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class OlapService {

    @Value("${magreport.olap.max-data-volume}")
    private long maxDataVolume = 1000;

    private static final int DEFAULT_MEASURE_TUPLES_COUNT = 1000;

    private final OlapDomainService olapDomainService;
    private final JobDomainService jobDomainService;
    private final OlapConfigurationDomainService olapConfigurationDomainService;
    private final ExcelReportDomainService excelReportDomainService;

    private final UserDomainService userDomainService;

    private final ObjectMapper objectMapper;

    public OlapCubeResponse getCube(OlapCubeRequest request) {

        jobDomainService.checkAccessForJob(request.getJobId());

        jobDomainService.updateJobStats(request.getJobId(), false, true, false);

        log.debug("Start processing cube");
        var startTime = System.currentTimeMillis();
        final var jobData = jobDomainService.getJobData(request.getJobId());
        var endTime = System.currentTimeMillis() - startTime;
        log.debug("Job data acquired: " + endTime);

        startTime = System.currentTimeMillis();
        final var sourceCube = olapDomainService.getCubeData(jobData);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Report data acquired: " + endTime);

        startTime = System.currentTimeMillis();
        final var checkedFilterRows = olapDomainService.filterCubeData(sourceCube, request.getFilterGroup());
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Cube filtered: " + endTime);

        startTime = System.currentTimeMillis();
        var measures = getRequestedMeasures(sourceCube, request.getColumnFields(), request.getRowFields(), checkedFilterRows);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Measures acquired: " + endTime);

        checkMaxDataVolume(measures, request.getMetrics());

        startTime = System.currentTimeMillis();
        var metricValues = calculateMetricsValues(measures, request.getMetrics(), request.getColumnFields(), request.getRowFields(), request.getMetricFilterGroup(), sourceCube, checkedFilterRows, false);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Metrics calculation: " + endTime);

        startTime = System.currentTimeMillis();
        var metricResults = collectMetricResult(metricValues, request.getMetrics());
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Metrics collection: " + endTime);

        startTime = System.currentTimeMillis();
        var sortedMetrics = sortedResults(request, sourceCube, metricResults, measures);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Metrics sorting: " + endTime);

        startTime = System.currentTimeMillis();
        var sortedMeasures = getResultMeasures(sortedMetrics, measures);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Get result measures: " + endTime);


        if (request.getMetricFilterGroup() != null && (!request.getMetricFilterGroup().getFilters().isEmpty() || !request.getMetricFilterGroup().getChildGroups().isEmpty())) {
            startTime = System.currentTimeMillis();
            var dataTypesMetrics = getDataTypesForMetrics(sourceCube, request.getMetrics());
            var metricResultFilters = olapDomainService.filterMetricResult(sortedMetrics, request.getMetricFilterGroup(), dataTypesMetrics);
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Metrics filtered: " + endTime);


            return getOlapCubeFilterMetricResponse(request, sortedMetrics, metricResultFilters, sortedMeasures, sourceCube);

        } else {

            var columValues = sortedMeasures.getL().stream().skip(request.getColumnsInterval().getFrom()).limit(request.getColumnsInterval().getCount()).toList();
            var rowValues = sortedMeasures.getR().stream().skip(request.getRowsInterval().getFrom()).limit(request.getRowsInterval().getCount()).toList();
            sortedMetrics = getPageResult(sortedMetrics, new Pair<>(columValues, rowValues), request);

            return new OlapCubeResponse()
                    .setColumnValues(columValues)
                    .setRowValues(rowValues)
                    .setMetricValues(getOlapMetricResponse(request, sortedMetrics, sourceCube))
                    .setTotalColumns(measures.getL().totalCount())
                    .setTotalRows(measures.getR().totalCount());
        }
    }

    private void checkMaxDataVolume(Pair<MeasureData, MeasureData> measures, List<MetricDefinition> metrics) {
        final var numColumns = (long) (measures.getL().values().isEmpty() ? 1 : measures.getL().values().size());
        final var numRows = (long) (measures.getR().values().isEmpty() ? 1 : measures.getR().values().size());
        final var numMetrics = (long) (metrics.isEmpty() ? 1 : metrics.size());

        if (numColumns * numRows * numMetrics > maxDataVolume) {
            throw new OlapMaxDataVolumeExceeded(
                    "???????????????? ???????????????????? ?????????? ???????????????????????????? ????????????:" +
                            "\n???????????????????????? ?????????? - " + maxDataVolume +
                            "\n?????????????????????????? ?????????? - " + numColumns * numRows * numMetrics +
                            "\n?????????? ?????????????? - " + numColumns +
                            "\n?????????? ?????????? - " + numRows +
                            "\n?????????? ???????????? - " + numMetrics
            );
        }
    }

    public OlapFieldItemsResponse getFieldValues(OlapFieldItemsRequest request) {

        log.debug("Start processing cube");
        var startTime = System.currentTimeMillis();
        var jobData = jobDomainService.getJobData(request.getJobId());
        var endTime = System.currentTimeMillis() - startTime;
        log.debug("Job data acquired: " + endTime);

        startTime = System.currentTimeMillis();
        var sourceCube = olapDomainService.getCubeData(jobData);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Report data acquired: " + endTime);

        startTime = System.currentTimeMillis();
        var checkedFilterRows = olapDomainService.filterCubeData(sourceCube, request.getFilterGroup());
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Cube filtered: " + endTime);

        if (request.getMetricFilterGroup() != null && (!request.getMetricFilterGroup().getFilters().isEmpty() || !request.getMetricFilterGroup().getChildGroups().isEmpty())) {

            startTime = System.currentTimeMillis();
            var measures = getRequestedMeasures(
                    sourceCube, request.getColumnFields(), request.getRowFields(),
                    checkedFilterRows);
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Measures acquired: " + endTime);

            startTime = System.currentTimeMillis();
            var metricValues = calculateMetricsValues(measures, request.getMetrics(), request.getColumnFields(), request.getRowFields(), request.getMetricFilterGroup(), sourceCube, checkedFilterRows, true);
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Metrics calculation: " + endTime);

            startTime = System.currentTimeMillis();
            var metricResults = collectMetricResult(metricValues, request.getMetrics());
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Metrics collection: " + endTime);

            startTime = System.currentTimeMillis();
            var metricResultFilters = olapDomainService.filterMetricResult(metricResults, request.getMetricFilterGroup(), getDataTypesForMetrics(sourceCube, request.getMetrics()));
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Metrics filtered: " + endTime);


            if (request.getRowFields().contains(request.getFieldId())) {
                int indexField = new ArrayList<>(request.getRowFields()).indexOf(request.getFieldId());
                var rowRange = collectRange(metricResultFilters.getC(), new Interval(0, metricResultFilters.getC().length));
                var rowMeasures = measures.getR().values().stream().toList();
                var values = rowRange.stream().map(rowMeasures::get).map(o -> o.get(indexField)).collect(Collectors.toSet());
                return new OlapFieldItemsResponse(getFieldValues(values, request));
            }


            if (request.getColumnFields().contains(request.getFieldId())) {
                int indexField = new ArrayList<>(request.getColumnFields()).indexOf(request.getFieldId());
                var columnRange = collectRange(metricResultFilters.getB(), new Interval(0, metricResultFilters.getB().length));
                var colMeasures = measures.getL().values().stream().toList();
                var values = columnRange.stream().map(colMeasures::get).map(o -> o.get(indexField)).collect(Collectors.toSet());
                return new OlapFieldItemsResponse(getFieldValues(values, request));
            }


            return new OlapFieldItemsResponse(getFieldValues(Collections.emptySet(), request));

        } else {

            int indexField = sourceCube.fieldIndexes().get(request.getFieldId());
            HashSet<String> values = new HashSet<>();

            for (int i = 0; i < sourceCube.numRows(); i++) {
                if (checkedFilterRows[i])
                    values.add(sourceCube.data()[indexField][i]);
            }

            return new OlapFieldItemsResponse(getFieldValues(values, request));
        }
    }

    public List<OlapInfoCubesResponse> getInfoAboutCubes() {
        return olapDomainService.getInfoAboutCubes();
    }

    public Path exportPivotTableExcel(OlapExportPivotTableRequest request) throws JsonProcessingException {
        request.getCubeRequest().getRowsInterval().setFrom(0).setCount(Integer.MAX_VALUE);
        request.getCubeRequest().getColumnsInterval().setFrom(0).setCount(Integer.MAX_VALUE);

        var user = userDomainService.getCurrentUser();

        var resultCube = getCube(request.getCubeRequest());
        var metadata = jobDomainService.getJobMetaData(request.getCubeRequest().getJobId());
        var config = olapConfigurationDomainService.getReportOlapConfiguration(request.getConfiguration());

        return excelReportDomainService.getExcelPivotTable(resultCube, metadata, objectMapper.readValue(config.getOlapConfig().getData(), HashMap.class), request, user.getId());

    }

    private Pair<MeasureData, MeasureData> getRequestedMeasures(
            CubeData cubeData,
            LinkedHashSet<Long> columnFields,
            LinkedHashSet<Long> rowFields,
            boolean[] checkedFilterRows) {
        final var columnTuples = new HashSet<List<String>>(DEFAULT_MEASURE_TUPLES_COUNT);
        final var rowTuples = new HashSet<List<String>>(DEFAULT_MEASURE_TUPLES_COUNT);

        final var columnFieldsIndexes = columnFields.stream().map(fieldId -> cubeData.fieldIndexes().get(fieldId)).toList();
        final var rowFieldsIndexes = rowFields.stream().map(fieldId -> cubeData.fieldIndexes().get(fieldId)).toList();

        var typesCol = columnFields.stream().map(cubeData.reportMetaData()::getTypeField).toList();
        var typesRow = rowFields.stream().map(cubeData.reportMetaData()::getTypeField).toList();

        var startTime = System.currentTimeMillis();
        for (int i = 0; i < cubeData.numRows(); i++) {
            if (checkedFilterRows[i]) {
                final var columnTuple = getTuple(columnFieldsIndexes, cubeData.data(), i);
                final var rowTuple = getTuple(rowFieldsIndexes, cubeData.data(), i);
                if (!columnTuple.isEmpty()) columnTuples.add(columnTuple);
                if (!rowTuple.isEmpty()) rowTuples.add(rowTuple);
            }
        }

        if (columnTuples.isEmpty()) columnTuples.add(Collections.emptyList());
        if (rowTuples.isEmpty()) rowTuples.add(Collections.emptyList());

        var endTime = System.currentTimeMillis() - startTime;
        log.debug("Measures tuples collected: " + endTime);

        startTime = System.currentTimeMillis();
        final Pair<Set<List<String>>, Set<List<String>>> result = new Pair<>(
                new LinkedHashSet<>(sortRowSet(columnTuples, typesCol)),
                new LinkedHashSet<>(sortRowSet(rowTuples, typesRow)));
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Measures tuples sorted and trimmed: " + endTime);

        return new Pair<>(new MeasureData(result.getL(), columnTuples.size()), new MeasureData(result.getR(), rowTuples.size()));
    }

    private Set<List<String>> sortRowSet(Set<List<String>> rowSet, List<DataTypeEnum> dataTypes) {
        return rowSet.stream().sorted((o1, o2) -> {

                    int i = 0;
                    int compare = 0;
                    while (i < dataTypes.size() && compare == 0) {

                        var var1 = o1.get(i);
                        var var2 = o2.get(i);

                        var continueFlag = false;
                        if (Objects.isNull(var1)) {
                            compare = -1;
                            continueFlag = true;
                        }
                        if (Objects.isNull(var2)) {
                            compare = 1;
                            continueFlag = true;
                        }
                        if (Objects.isNull(var1) && Objects.isNull(var2)) compare = 0;

                        if (continueFlag) {
                            i++;
                            continue;
                        }


                        compare = switch (dataTypes.get(i)) {
                            case INTEGER -> Integer.compare(Integer.parseInt(var1), Integer.parseInt(var2));
                            case STRING -> var1.compareTo(var2);
                            case DOUBLE -> Double.compare(Double.parseDouble(var1), Double.parseDouble(var2));
                            case DATE -> LocalDate.parse(var1).compareTo(LocalDate.parse(var2));
                            case TIMESTAMP -> {
                                var time1 = LocalDateTime.parse(var1.replace(" ", "T"));
                                var time2 = LocalDateTime.parse(var2.replace(" ", "T"));
                                yield time1.compareTo(time2);
                            }
                            case BOOLEAN -> Boolean.compare(Boolean.parseBoolean(var1), Boolean.parseBoolean(var2));
                        };
                        i++;
                    }
                    return compare;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private MetricsFunction[][][] calculateMetricsValues(
            Pair<MeasureData, MeasureData> measures,
            List<MetricDefinition> metrics,
            LinkedHashSet<Long> columnFields,
            LinkedHashSet<Long> rowFields,
            MetricFilterGroup metricFilterGroup,
            CubeData reportCube,
            boolean[] checkedFilterRows,
            boolean calcForFilter) {
        final var result = new MetricsFunction[measures.getL().values().size()][measures.getR().values().size()][metrics.size()];
        final var columnIndices = indexSet(measures.getL().values());
        final var rowIndices = indexSet(measures.getR().values());
        if (columnIndices.isEmpty() && rowIndices.isEmpty()) return result;

        final var columnFieldsIndexes = columnFields.stream().map(fieldId -> reportCube.fieldIndexes().get(fieldId)).toList();
        final var rowFieldsIndexes = rowFields.stream().map(fieldId -> reportCube.fieldIndexes().get(fieldId)).toList();
        final var metricFieldsIndexes = metrics.stream().map(MetricDefinition::getFieldId).map(fieldId -> reportCube.fieldIndexes().get(fieldId)).toList();
        final var columnsTupleHolder = Extensions.<String>initList(columnFields.size());
        final var rowsTupleHolder = Extensions.<String>initList(rowFields.size());

        final var filtersMetrics = metricFilterGroup == null ? Collections.<Integer>emptySet() : metricFilterGroup.getAllMetricIds();

        final var countDistinctCaches = metrics.stream().map(o -> new HashSet<Triple<Integer, Integer, String>>()).toList();
        final var metricDataTypes = IntStream.range(0, metrics.size()).boxed()
                .collect(Collectors.toMap(Function.identity(), idx -> reportCube.reportMetaData().getTypeField(metrics.get(idx).getFieldId())));
        final var metricAggregationTypes = IntStream.range(0, metrics.size()).boxed()
                .collect(Collectors.toMap(Function.identity(), idx -> metrics.get(idx).getAggregationType()));

        for (int cubeRow = 0; cubeRow < reportCube.numRows(); cubeRow++) {
            if (!checkedFilterRows[cubeRow]) continue;
            final var columnsTuple = getTuple(columnFieldsIndexes, reportCube.data(), cubeRow, columnsTupleHolder);
            final var rowsTuple = getTuple(rowFieldsIndexes, reportCube.data(), cubeRow, rowsTupleHolder);
            final var columnNumber = columnIndices.getOrDefault(columnsTuple, -1);
            final var rowNumber = rowIndices.getOrDefault(rowsTuple, -1);

            if (columnNumber > -1 && rowNumber > -1) {
                for (int metricNumber = 0; metricNumber < metrics.size(); metricNumber++) {
                    if (calcForFilter && !filtersMetrics.contains(metricNumber)) continue;

                    if (result[columnNumber][rowNumber][metricNumber] == null) {
                        result[columnNumber][rowNumber][metricNumber] = metricAggregationTypes.get(metricNumber).getMetricsFunction(metricDataTypes.get(metricNumber), countDistinctCaches.get(metricNumber));
                    }
                    final var metricColumn = metricFieldsIndexes.get(metricNumber);

                    result[columnNumber][rowNumber][metricNumber].addValue(reportCube.data()[metricColumn][cubeRow], columnNumber, rowNumber);
                }
            }
        }

        return result;
    }


    private MetricResult[][][] collectMetricResult(MetricsFunction[][][] metrics, List<MetricDefinition> metricsDef) {

        MetricResult[][][] results = new MetricResult[metrics.length][metrics.length == 0 ? 0 : metrics[0].length][metricsDef.size()];

        for (int col = 0; col < metrics.length; col++)
            for (int row = 0; row < metrics[col].length; row++)
                for (int metric = 0; metric < metrics[col][row].length; metric++) {
                    var value = metrics[col][row][metric] == null ? "" : metrics[col][row][metric].calculate(col, row);
                    results[col][row][metric] = new MetricResult(value, col, row);
                }

        return results;
    }

    private List<OlapMetricResponse> getOlapMetricResponse(OlapCubeRequest request, MetricResult[][][] result, CubeData reportCube) {

        AtomicLong index = new AtomicLong(0);
        List<List<List<String>>> valuesMetrics = new ArrayList<>();

        for (int i = 0; i < request.getMetrics().size(); i++) {
            var values = new ArrayList<List<String>>();
            for (MetricResult[][] columns : result) {
                var valueRows = new ArrayList<String>();
                for (MetricResult[] rows : columns) {
                    var value = rows[i];
                    valueRows.add(value == null ? "" : value.getValue());
                }
                values.add(valueRows);
            }
            valuesMetrics.add(values);
        }

        var responses = new ArrayList<OlapMetricResponse>();
        request.getMetrics().forEach(metric -> {
            var response = new OlapMetricResponse()
                    .setFieldId(metric.getFieldId())
                    .setAggregationType(metric.getAggregationType())
                    .setDataType(metric.getAggregationType().getDataTypeMetricFunction(reportCube.reportMetaData().getTypeField(metric.getFieldId())))
                    .setValues(valuesMetrics.get((int) index.getAndIncrement()));
            responses.add(response);
        });

        return responses;
    }

    private OlapCubeResponse getOlapCubeFilterMetricResponse(
            OlapCubeRequest request, MetricResult[][][] metrics, Triple<boolean[][], boolean[], boolean[]> filterResult, Pair<List<List<String>>, List<List<String>>> measures, CubeData reportCube) {

        var result = new OlapMetricResponse2[request.getMetrics().size()];

        var columnRange = collectRange(filterResult.getB(), request.getColumnsInterval());
        var rowRange = collectRange(filterResult.getC(), request.getRowsInterval());

        for (int metric = 0; metric < request.getMetrics().size(); metric++) {
            result[metric] = new OlapMetricResponse2();
            result[metric].setValues(new String[columnRange.size()][rowRange.size()]);
            result[metric].setAggregationType(request.getMetrics().get(metric).getAggregationType());
            result[metric].setFieldId(request.getMetrics().get(metric).getFieldId());
        }

        var columns = new LinkedHashMap<Integer, List<String>>();
        var rows = new LinkedHashMap<Integer, List<String>>();

        final int totalColumns = countingItems(filterResult.getB());
        final int totalRows = countingItems(filterResult.getC());

        for (int colIndex = 0; colIndex < columnRange.size(); colIndex++)
            for (int rowIndex = 0; rowIndex < rowRange.size(); rowIndex++)
                for (int metric = 0; metric < request.getMetrics().size(); metric++) {

                    result[metric].getValues()[colIndex][rowIndex] = metrics[columnRange.get(colIndex)][rowRange.get(rowIndex)][metric].getValue();

                    if (!columns.containsKey(colIndex))
                        columns.put(columnRange.get(colIndex) - request.getColumnsInterval().getFrom(), measures.getL().get(metrics[columnRange.get(colIndex)][rowRange.get(rowIndex)][metric].getColumn()));

                    if (!rows.containsKey(rowIndex))
                        rows.put(rowRange.get(rowIndex) - request.getRowsInterval().getFrom(), measures.getR().get(metrics[columnRange.get(colIndex)][rowRange.get(rowIndex)][metric].getRow()));

                }


        return new OlapCubeResponse()
                .setTotalColumns(totalColumns)
                .setTotalRows(totalRows)
                .setColumnValues(columns.values().stream().toList())
                .setRowValues(rows.values().stream().toList())
                .setMetricValues(
                        Arrays.stream(result)
                                .map(r -> {
                                    var response = new OlapMetricResponse();
                                    response.setFieldId(r.getFieldId())
                                            .setAggregationType(r.getAggregationType())
                                            .setDataType(r.getAggregationType().getDataTypeMetricFunction(reportCube.reportMetaData().getTypeField(r.getFieldId())))
                                            .setValues(Arrays.stream(r.getValues()).map(f -> Arrays.stream(f).toList()).collect(Collectors.toList()));
                                    if (response.getValues().isEmpty())
                                        response.getValues().add(Collections.singletonList(""));

                                    return response;
                                }).toList());
    }


    private List<String> getTuple(List<Integer> columnIndexes, String[][] cubeData, int rowNumber, List<String> tupleHolder) {
        for (int i = 0; i < tupleHolder.size(); i++) {
            tupleHolder.set(i, cubeData[columnIndexes.get(i)][rowNumber]);
        }
        return tupleHolder;
    }

    private List<String> getTuple(List<Integer> columnIndexes, String[][] cubeData, int rowNumber) {
        final var tupleHolder = new ArrayList<String>(columnIndexes.size());
        for (Integer columnIndex : columnIndexes) {
            tupleHolder.add(cubeData[columnIndex][rowNumber]);
        }
        return tupleHolder;
    }

    private Map<List<String>, Integer> indexSet(Set<List<String>> source) {
        final var result = new HashMap<List<String>, Integer>();
        final var counter = new AtomicInteger(0);

        source.forEach(element -> result.put(element, counter.getAndIncrement()));

        return result;
    }

    private Pair<String[], Long> getFieldValues(Set<String> values, OlapFieldItemsRequest request) {

        String[] sortedValues = values.toArray(new String[0]);
        Arrays.sort(sortedValues, Comparator.nullsFirst(Comparator.naturalOrder()));

        if (request.getFrom() >= sortedValues.length) {
            return new Pair<>(new String[0], (long) values.size());
        } else {
            if (request.getEndPoint() >= sortedValues.length)
                return new Pair<>(Arrays.copyOfRange(sortedValues, request.getFrom().intValue(), sortedValues.length), (long) values.size());
            else
                return new Pair<>(Arrays.copyOfRange(sortedValues, request.getFrom().intValue(), request.getEndPoint()), (long) values.size());
        }
    }

    private MetricResult[][][] sortedResults(OlapCubeRequest request, CubeData reportCube, MetricResult[][][] result, Pair<MeasureData, MeasureData> measures) {

        var metricTypes =
                request
                        .getMetrics()
                        .stream()
                        .map(metric -> {
                            if (metric.getAggregationType().equals(AggregationType.COUNT) || metric.getAggregationType().equals(AggregationType.COUNT_DISTINCT))
                                return DataTypeEnum.INTEGER;
                            else
                                return reportCube.reportMetaData().getTypeField(metric.getFieldId());
                        })
                        .toList();

        if (!request.getRowSort().isEmpty()) {
            sortMetrics(result, metricTypes, SortDirection.Row, getSorting(measures.getR().values().stream().toList(), request.getRowSort()));
        }

        if (!request.getColumnSort().isEmpty()) {

            var defaultColumnValues = measures.getL().values().stream().toList();
            var newColumnValues = new ArrayList<List<String>>(defaultColumnValues.size());

            for (int col = 0; col < result.length; col++) {
                var oldIndex = result[col][0][0].getColumn();
                newColumnValues.add(col, defaultColumnValues.get(oldIndex));
            }

            result = transposeResults(result, request.getMetrics().size());
            sortMetrics(result, metricTypes, SortDirection.Column, getSorting(newColumnValues, request.getColumnSort()));
            result = transposeResults(result, request.getMetrics().size());
        }


        return result;
    }

    private void sortMetrics(MetricResult[][][] metrics, List<DataTypeEnum> dataTypes, SortDirection sortDirection, List<Sorting> sortings) {

        if (metrics.length > 0) {
            var rowIndex = new int[sortings.size()][metrics[0].length];

            for (int sortingIndex = 0; sortingIndex < sortings.size(); sortingIndex++)
                for (int i = 0; i < metrics[0].length; i++) {
                    var row = switch (sortDirection) {
                        case Column -> metrics[0][i][0].getColumn();
                        case Row -> metrics[0][i][0].getRow();
                    };
                    rowIndex[sortingIndex][row] = i;
                }


            Arrays.sort(metrics, (row1, row2) -> {
                var result = 0;
                for (Sorting sorting : sortings) {
                    if (sorting.getTupleIndex() == -1) continue;

                    int col = rowIndex[sortings.indexOf(sorting)][sorting.getTupleIndex()];
                    int row = sorting.getMetricId();

                    var value1 = row1[col][row];
                    var value2 = row2[col][row];

                    var type = dataTypes.get(sorting.getMetricId());
                    var compare = switch (type) {
                        case INTEGER -> {
                            var v1 = value1.getValue().isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(value1.getValue());
                            var v2 = value2.getValue().isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(value2.getValue());
                            yield Integer.compare(v1, v2);
                        }
                        case STRING, DATE, TIMESTAMP, BOOLEAN -> value1.getValue().compareTo(value2.getValue());
                        case DOUBLE -> {
                            var v1 = value1.getValue().isEmpty() ? Double.MIN_VALUE : Double.parseDouble(value1.getValue());
                            var v2 = value2.getValue().isEmpty() ? Double.MIN_VALUE : Double.parseDouble(value2.getValue());
                            yield Double.compare(v1, v2);
                        }
                    };

                    if (compare == 0)
                        compare = switch (sortDirection) {
                            case Column -> Integer.compare(value1.getRow(), value2.getRow());
                            case Row -> Integer.compare(value1.getColumn(), value2.getColumn());
                        };

                    return sorting.getOrder().equals(SortingOrder.Ascending) ? compare : compare * -1;
                }

                return result;
            });
        }
    }

    private MetricResult[][][] transposeResults(MetricResult[][][] metrics, int metricCount) {

        MetricResult[][][] result = new MetricResult[metrics[0].length][metrics.length][metricCount];

        for (int col = 0; col < metrics.length; col++)
            for (int row = 0; row < metrics[col].length; row++)
                result[row][col] = metrics[col][row].clone();
        return result;

    }

    private List<Sorting> getSorting(List<List<String>> measure, List<SortingParams> params) {

        return params.stream()
                .map(param -> new Sorting(
                        param.getOrder(),
                        measure.stream().toList().indexOf(param.getTuple()),
                        param.getMetricId()
                )).toList();
    }

    private Pair<List<List<String>>, List<List<String>>> getResultMeasures(MetricResult[][][] metrics, Pair<MeasureData, MeasureData> measures) {

        var defaultColumnValues = measures.getL().values().stream().toList();
        var defaultRowValues = measures.getR().values().stream().toList();

        var resultColumnValues = new ArrayList<List<String>>(defaultColumnValues.size());
        var resultRowValues = new ArrayList<List<String>>(defaultRowValues.size());

        if (metrics.length == 0) return new Pair<>(defaultColumnValues, defaultRowValues);
        if (metrics[0].length == 0) return new Pair<>(defaultColumnValues, defaultRowValues);
        if (metrics[0][0].length == 0) return new Pair<>(defaultColumnValues, defaultRowValues);


        for (int col = 0; col < metrics.length; col++) {
            var oldIndex = metrics[col][0][0].getColumn();
            resultColumnValues.add(col, defaultColumnValues.get(oldIndex));
        }


        for (int row = 0; row < metrics[0].length; row++) {
            var oldIndex = metrics[0][row][0].getRow();
            resultRowValues.add(row, defaultRowValues.get(oldIndex));
        }


        return new Pair<>(resultColumnValues, resultRowValues);

    }

    private List<Integer> collectRange(boolean[] filter, Interval interval) {

        var result = new ArrayList<Integer>();
        var skip = interval.getFrom();
        var take = interval.getCount();

        for (int i = 0; i < filter.length; i++) {
            if (!filter[i]) {
                if (skip > 0) {
                    skip -= 1;
                } else if (take > 0) {
                    result.add(i);
                    take -= 1;
                } else break;
            }
        }
        return result;
    }

    private int countingItems(boolean[] items) {
        int result = 0;
        for (boolean item : items) {
            if (!item) result++;
        }
        return result;
    }

    private List<DataTypeEnum> getDataTypesForMetrics(CubeData reportCube, List<MetricDefinition> request) {
        return request.stream().map(m -> m.getAggregationType().getDataTypeMetricFunction(reportCube.reportMetaData().getTypeField(m.getFieldId()))).toList();
    }

    private MetricResult[][][] getPageResult(MetricResult[][][] metricResults, Pair<List<List<String>>, List<List<String>>> measures, OlapCubeRequest request) {

        MetricResult[][][] result = new MetricResult[measures.getL().size()][measures.getR().size()][request.getMetrics().size()];

        for (int column = 0; column < result.length; column++)
            for (int row = 0; row < result[0].length; row++)
                System.arraycopy(metricResults[column + request.getColumnsInterval().getFrom()][row + request.getRowsInterval().getFrom()], 0, result[column][row], 0, request.getMetrics().size());

        return result;
    }

}
