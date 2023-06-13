package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.domain.olap.SortDirection;
import ru.magnit.magreportbackend.domain.olap.SortingOrder;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.olap.MeasureData;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.inner.olap.Sorting;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.Interval;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestV2;
import ru.magnit.magreportbackend.dto.request.olap.SortingParams;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponseV2;
import ru.magnit.magreportbackend.dto.response.olap.OlapInfoCubesResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapMetricResponseV2;
import ru.magnit.magreportbackend.exception.OlapMaxDataVolumeExceeded;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.service.domain.ExcelReportDomainService;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapDomainService;
import ru.magnit.magreportbackend.service.domain.OlapUserChoiceDomainService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("Duplicates")
public class OlapServiceV2 {
    private static final int DEFAULT_MEASURE_TUPLES_COUNT = 1000;

    @Value("${magreport.olap.max-data-volume}")
    private long maxDataVolume = 1000;

    private final JobDomainService jobDomainService;
    private final UserDomainService userDomainService;
    private final OlapUserChoiceDomainService olapUserChoiceDomainService;
    private final OlapDomainService olapDomainService;
    private final DerivedFieldService derivedFieldService;
    private final ExcelReportDomainService excelReportDomainService;

    public OlapCubeResponseV2 getCube(OlapCubeRequestV2 request) {
        var currentUser = userDomainService.getCurrentUser();
        jobDomainService.checkAccessForJob(request.getJobId());

        jobDomainService.updateJobStats(request.getJobId(), false, true, false);

        log.debug("Start processing cube");
        var startTime = System.currentTimeMillis();
        final var jobData = jobDomainService.getJobData(request.getJobId());
        var endTime = System.currentTimeMillis() - startTime;
        log.debug("Job data acquired: " + endTime);

        olapUserChoiceDomainService.setOlapUserChoice(jobData.reportId(), currentUser.getId(), true);

        startTime = System.currentTimeMillis();
        var sourceCube = olapDomainService.getCubeData(jobData);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Report data acquired: " + endTime);

        var cubeRequest = request;
        if (request.hasDerivedFields()) {
            startTime = System.currentTimeMillis();
            final var result = derivedFieldService.preProcessCubeV2(sourceCube, request);
            sourceCube = result.getL();
            cubeRequest = result.getR();
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Derived fields calculated: " + endTime);
        }

        startTime = System.currentTimeMillis();
        final var checkedFilterRows = olapDomainService.filterCubeData(sourceCube, cubeRequest.getFilterGroup().asFilterGroup(request.getFieldIndexes()));
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Cube filtered: " + endTime);

        startTime = System.currentTimeMillis();
        var measures = getRequestedMeasures(sourceCube, getFieldsPositions(cubeRequest.getColumnFields()), getFieldsPositions(cubeRequest.getRowFields()), checkedFilterRows);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Measures acquired: " + endTime);

        checkMaxDataVolume(measures, cubeRequest.getMetrics());

        startTime = System.currentTimeMillis();
        var metricValues = calculateMetricsValues(measures, cubeRequest, getFieldsPositions(cubeRequest.getColumnFields()), getFieldsPositions(cubeRequest.getRowFields()), cubeRequest.getMetricFilterGroup(), sourceCube, checkedFilterRows, false);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Metrics calculation: " + endTime);


        startTime = System.currentTimeMillis();
        var collectMetricResults = collectMetricResult(metricValues, cubeRequest.getMetrics());
        var metricResults = collectMetricResults.getL();
        var metricDataTypes = collectMetricResults.getR();
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Metrics collection: " + endTime);

        startTime = System.currentTimeMillis();
        var sortedMetrics = sortedResults(cubeRequest, metricResults, measures);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Metrics sorting: " + endTime);

        startTime = System.currentTimeMillis();
        var sortedMeasures = getResultMeasures(sortedMetrics, measures);
        endTime = System.currentTimeMillis() - startTime;
        log.debug("Get result measures: " + endTime);


        if (cubeRequest.getMetricFilterGroup() != null && (!cubeRequest.getMetricFilterGroup().getFilters().isEmpty() || !cubeRequest.getMetricFilterGroup().getChildGroups().isEmpty())) {
            startTime = System.currentTimeMillis();
            var metricResultFilters = olapDomainService.filterMetricResult(sortedMetrics, cubeRequest.getMetricFilterGroup(), metricDataTypes);
            endTime = System.currentTimeMillis() - startTime;
            log.debug("Metrics filtered: " + endTime);


            return getOlapCubeFilterMetricResponse(cubeRequest, sortedMetrics, metricResultFilters, sortedMeasures, metricDataTypes);

        } else {

            var columValues = sortedMeasures.getL().stream().skip(cubeRequest.getColumnsInterval().getFrom()).limit(cubeRequest.getColumnsInterval().getCount()).toList();
            var rowValues = sortedMeasures.getR().stream().skip(cubeRequest.getRowsInterval().getFrom()).limit(cubeRequest.getRowsInterval().getCount()).toList();
            sortedMetrics = getPageResult(sortedMetrics, new Pair<>(columValues, rowValues), cubeRequest);

            return new OlapCubeResponseV2()
                    .setColumnValues(columValues)
                    .setRowValues(rowValues)
                    .setMetricValues(getOlapMetricResponse(cubeRequest, sortedMetrics, metricDataTypes))
                    .setTotalColumns(measures.getL().totalCount())
                    .setTotalRows(measures.getR().totalCount());
        }
    }

    public List<OlapInfoCubesResponse> getInfoAboutCubes() {
        return olapDomainService.getInfoAboutCubes();
    }


    public Path getExcelPivotPath(Long jobId, Long code) {
        return excelReportDomainService.getExcelPivotPath(jobId, code);
    }

    private Pair<MetricResult[][][], List<DataTypeEnum>> collectMetricResult(BaseExpression[][][] metrics, List<FieldExpressionResponse> metricsDef) {

        MetricResult[][][] results = new MetricResult[metrics.length][metrics.length == 0 ? 0 : metrics[0].length][metricsDef.size()];
        var types = new DataTypeEnum[metricsDef.size()];

        for (int col = 0; col < metrics.length; col++)
            for (int row = 0; row < metrics[col].length; row++)
                for (int metric = 0; metric < metrics[col][row].length; metric++) {
                    Pair<String, DataTypeEnum> value = metrics[col][row][metric] == null ? new Pair<>("", null) : metrics[col][row][metric].calculate(row);
                    results[col][row][metric] = new MetricResult(value.getL(), value.getR(), col, row);
                    if (Objects.nonNull(value.getR()))
                        types[metric] = value.getR();
                }

        for (int i = 0; i < types.length; i++){
            if (Objects.isNull(types[i]))
                types[i] = DataTypeEnum.STRING;
        }

        return new Pair<>(results, Arrays.asList(types));
    }

    private MetricResult[][][] sortedResults(OlapCubeRequestV2 request, MetricResult[][][] result, Pair<MeasureData, MeasureData> measures) {

        if (!request.getRowSort().isEmpty()) {
            sortMetrics(result, SortDirection.Row, getSorting(measures.getR().values().stream().toList(), request.getRowSort()));
        }

        if (!request.getColumnSort().isEmpty()) {

            var defaultColumnValues = measures.getL().values().stream().toList();
            var newColumnValues = new ArrayList<List<String>>(defaultColumnValues.size());

            for (int col = 0; col < result.length; col++) {
                var oldIndex = result[col][0][0].getColumn();
                newColumnValues.add(col, defaultColumnValues.get(oldIndex));
            }

            result = transposeResults(result, request.getMetrics().size());
            sortMetrics(result, SortDirection.Column, getSorting(newColumnValues, request.getColumnSort()));
            result = transposeResults(result, request.getMetrics().size());
        }


        return result;
    }

    private void sortMetrics(MetricResult[][][] metrics, SortDirection sortDirection, List<Sorting> sortings) {

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

                    var val1 = row1[col][row];
                    var val2 = row2[col][row];

                     var value1 =  Objects.isNull(val1) ? "" : val1.getValue();
                     var value2 = Objects.isNull(val2) ? "" : val2.getValue();

                     DataTypeEnum type = DataTypeEnum.STRING;
                     var type1 = val1.getType();
                     var type2 = val2.getType();

                     if (Objects.nonNull(type1)) type = type1;
                     else if (Objects.nonNull(type2)) type = type2;


                    var compare = switch (type) {
                        case INTEGER -> {
                            var v1 = value1.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(value1);
                            var v2 = value2.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(value2);
                            yield Integer.compare(v1, v2);
                        }
                        case STRING, DATE, TIMESTAMP, BOOLEAN -> value1.compareTo(value2);
                        case DOUBLE -> {
                            var v1 = value1.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(value1);
                            var v2 = value2.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(value2);
                            yield Double.compare(v1, v2);
                        }
                    };

                    if (compare == 0)
                        compare = switch (sortDirection) {
                            case Column -> Integer.compare(val1.getRow(), val2.getRow());
                            case Row -> Integer.compare(val1.getColumn(), val2.getColumn());
                        };

                    return sorting.getOrder().equals(SortingOrder.Ascending) ? compare : compare * -1;
                }

                return result;
            });
        }
    }

    private List<Sorting> getSorting(List<List<String>> measure, List<SortingParams> params) {

        return params.stream()
                .map(param -> new Sorting(
                        param.getOrder(),
                        measure.stream().toList().indexOf(param.getTuple()),
                        param.getMetricId()
                )).toList();
    }

    private MetricResult[][][] transposeResults(MetricResult[][][] metrics, int metricCount) {

        MetricResult[][][] result = new MetricResult[metrics[0].length][metrics.length][metricCount];

        for (int col = 0; col < metrics.length; col++)
            for (int row = 0; row < metrics[col].length; row++)
                result[row][col] = metrics[col][row].clone();
        return result;

    }

    private BaseExpression[][][] calculateMetricsValues(
            Pair<MeasureData, MeasureData> measures,
            OlapCubeRequestV2 request,
            Set<Long> columnFields,
            Set<Long> rowFields,
            MetricFilterGroup metricFilterGroup,
            CubeData reportCube,
            boolean[] checkedFilterRows,
            boolean calcForFilter) {
        final var metrics = request.getMetrics();
        final var result = new BaseExpression[measures.getL().values().size()][measures.getR().values().size()][metrics.size()];
        final var columnIndices = indexSet(measures.getL().values());
        final var rowIndices = indexSet(measures.getR().values());
        if (columnIndices.isEmpty() && rowIndices.isEmpty()) return result;

        final var columnFieldsIndexes = columnFields.stream().map(fieldId -> reportCube.fieldIndexes().get(fieldId)).toList();
        final var rowFieldsIndexes = rowFields.stream().map(fieldId -> reportCube.fieldIndexes().get(fieldId)).toList();
        final var columnsTupleHolder = Extensions.<String>initList(columnFields.size());
        final var rowsTupleHolder = Extensions.<String>initList(rowFields.size());

        final var filtersMetrics = metricFilterGroup == null ? Collections.<Integer>emptySet() : metricFilterGroup.getAllMetricIds();

        final var countDistinctSets = new IdentityHashMap<FieldExpressionResponse, Set<Triple<Integer, Integer, String>>>();

        final var fieldIndexes = getFieldIndexes(request, reportCube);
        metrics.stream()
                .flatMap(def -> def.getAllExpressions().stream())
                .filter(def -> def.getType() == Expressions.COUNT_DISTINCT)
                .forEach(def -> countDistinctSets.put(def, new HashSet<>()));

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
                        final var metricExpression = metrics.get(metricNumber);
                        result[columnNumber][rowNumber][metricNumber] = metricExpression.getType().init(metricExpression, new ExpressionCreationContext(fieldIndexes, reportCube.data(), null, countDistinctSets));
                    }

                    result[columnNumber][rowNumber][metricNumber].addValue(cubeRow, columnNumber, rowNumber);
                }
            }
        }

        return result;
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

    private Map<List<String>, Integer> indexSet(Set<List<String>> source) {
        final var result = new HashMap<List<String>, Integer>();
        final var counter = new AtomicInteger(0);

        source.forEach(element -> result.put(element, counter.getAndIncrement()));

        return result;
    }

    private void checkMaxDataVolume(Pair<MeasureData, MeasureData> measures, List<FieldExpressionResponse> metrics) {
        final var numColumns = (long) (measures.getL().values().isEmpty() ? 1 : measures.getL().values().size());
        final var numRows = (long) (measures.getR().values().isEmpty() ? 1 : measures.getR().values().size());
        final var numMetrics = (long) (metrics.isEmpty() ? 1 : metrics.size());

        if (numColumns * numRows * numMetrics > maxDataVolume) {
            throw new OlapMaxDataVolumeExceeded(
                    "Превышен допустимый объем обрабатываемых данных:" +
                            "\nМаксимальный объем - " + maxDataVolume +
                            "\nЗапрашиваемый объем - " + numColumns * numRows * numMetrics +
                            "\nКол-во столбцов - " + numColumns +
                            "\nКол-во строк - " + numRows +
                            "\nКол-во метрик - " + numMetrics
            );
        }
    }

    private Set<Long> getFieldsPositions(Set<FieldDefinition> source) {
        return source.stream().map(FieldDefinition::getFieldId).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Pair<MeasureData, MeasureData> getRequestedMeasures(CubeData cubeData, Set<Long> columnFields, Set<Long> rowFields, boolean[] checkedFilterRows) {
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
                            case INTEGER -> Long.compare(Long.parseLong(var1), Long.parseLong(var2));
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

    private Map<FieldDefinition, Pair<Integer, DataTypeEnum>> getFieldIndexes(OlapCubeRequestV2 request, CubeData cubeData) {
        final var result = new HashMap<FieldDefinition, Pair<Integer, DataTypeEnum>>();
        request.getColumnFields().forEach(f ->
                result.put(f, new Pair<>(cubeData.fieldIndexes().get(f.getFieldId()), cubeData.reportMetaData().getTypeField(f.getFieldId())))
        );

        request.getRowFields().forEach(f ->
                result.put(f, new Pair<>(cubeData.fieldIndexes().get(f.getFieldId()), cubeData.reportMetaData().getTypeField(f.getFieldId())))
        );

        request.getMetrics().forEach(f ->
                result.put(new FieldDefinition(f.getReferenceId(), f.getFieldType()), new Pair<>(cubeData.fieldIndexes().get(f.getReferenceId()), cubeData.reportMetaData().getTypeField(f.getReferenceId())))
        );

        return result;
    }

    private OlapCubeResponseV2 getOlapCubeFilterMetricResponse(
            OlapCubeRequestV2 request, MetricResult[][][] metrics, Triple<boolean[][], boolean[], boolean[]> filterResult, Pair<List<List<String>>, List<List<String>>> measures, List<DataTypeEnum> metricDataTypes ) {

        var result = new OlapMetricResponseV2[request.getMetrics().size()];

        var columnRange = collectRange(filterResult.getB(), request.getColumnsInterval());
        var rowRange = collectRange(filterResult.getC(), request.getRowsInterval());

        for (int metric = 0; metric < request.getMetrics().size(); metric++) {
            result[metric] = new OlapMetricResponseV2();
            result[metric].setValues(new String[columnRange.size()][rowRange.size()]);
            result[metric].setDataType(metricDataTypes.get(metric));
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

        return new OlapCubeResponseV2()
                .setTotalRows(totalRows)
                .setTotalColumns(totalColumns)
                .setColumnValues(columns.values().stream().toList())
                .setRowValues(rows.values().stream().toList())
                .setMetricValues(Arrays.asList(result));

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

    private MetricResult[][][] getPageResult(MetricResult[][][] metricResults, Pair<List<List<String>>, List<List<String>>> measures, OlapCubeRequestV2 request) {

        MetricResult[][][] result = new MetricResult[measures.getL().size()][measures.getR().size()][request.getMetrics().size()];

        for (int column = 0; column < result.length; column++)
            for (int row = 0; row < result[0].length; row++)
                System.arraycopy(metricResults[column + request.getColumnsInterval().getFrom()][row + request.getRowsInterval().getFrom()], 0, result[column][row], 0, request.getMetrics().size());

        return result;
    }

    private List<OlapMetricResponseV2> getOlapMetricResponse(OlapCubeRequestV2 request, MetricResult[][][] result, List<DataTypeEnum> dataTypeMetrics) {

        var valuesMetrics = new OlapMetricResponseV2[request.getMetrics().size()];

        for (int metric = 0; metric < request.getMetrics().size(); metric++) {
            valuesMetrics[metric] = new OlapMetricResponseV2();
            valuesMetrics[metric].setValues(new String[result.length][result[0].length]);
            valuesMetrics[metric].setDataType(dataTypeMetrics.get(0));
        }

        for (int colIndex = 0; colIndex < result.length; colIndex++)
            for (int rowIndex = 0; rowIndex < result[0].length; rowIndex++)
                for (int metric = 0; metric < request.getMetrics().size(); metric++) {
                    valuesMetrics[metric].getValues()[colIndex][rowIndex] = result[colIndex][rowIndex][metric].getValue();
                }

                    return Arrays.asList(valuesMetrics);
    }
}
