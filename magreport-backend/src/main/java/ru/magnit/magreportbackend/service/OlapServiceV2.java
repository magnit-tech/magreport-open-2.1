package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.inner.olap.MeasureData;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestV2;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponseV2;
import ru.magnit.magreportbackend.exception.OlapMaxDataVolumeExceeded;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.service.domain.JobDomainService;
import ru.magnit.magreportbackend.service.domain.OlapDomainService;
import ru.magnit.magreportbackend.service.domain.OlapUserChoiceDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;
import ru.magnit.magreportbackend.util.Extensions;
import ru.magnit.magreportbackend.util.Pair;
import ru.magnit.magreportbackend.util.Triple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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


        return new OlapCubeResponseV2();
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
                        result[columnNumber][rowNumber][metricNumber] = metricExpression.getType().init(metricExpression, new ExpressionCreationContext(request.getFieldIndexes(), reportCube.data(), null, countDistinctSets));
                    }

                    result[columnNumber][rowNumber][metricNumber].addValue(cubeRow, columnNumber, rowNumber);
                }
            }
        }

        return result;
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
}
