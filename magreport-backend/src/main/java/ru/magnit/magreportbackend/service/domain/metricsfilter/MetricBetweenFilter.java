package ru.magnit.magreportbackend.service.domain.metricsfilter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterDefinition;
import ru.magnit.magreportbackend.exception.InvalidParametersException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MetricBetweenFilter implements MetricFilterNode {

    private boolean invert;
    private List<String> values;
    private MetricResult[][][] data;
    private int metric;
    private DataTypeEnum type;
    private int rounding;

    @Override
    public MetricFilterNode init(MetricResult[][][] metricResults, MetricFilterDefinition filterDefinition, List<DataTypeEnum> metricsDataTypes) {
        invert = filterDefinition.isInvertResult();
        values = filterDefinition.getValues().stream().sorted().toList();
        data = metricResults;
        metric = filterDefinition.getMetricId().intValue();
        type = metricsDataTypes.get(filterDefinition.getMetricId().intValue());
        rounding = filterDefinition.getRounding();
        if (values.size() != 2) throw new InvalidParametersException("Incorrect number of filter arguments");
        return this;
    }

    @Override
    public boolean filter(int column, int row) {

        var currentVal = data[column][row][metric].getValue() == null ? "" : data[column][row][metric].getValue();

        var result = switch (type) {
            case INTEGER, DOUBLE -> {
                var current = currentVal.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(currentVal);
                var filterStart = values.get(0).isEmpty() ? Double.MIN_VALUE : Double.parseDouble(values.get(0));
                var filterStop = values.get(1).isEmpty() ? Double.MIN_VALUE : Double.parseDouble(values.get(1));

                yield BigDecimal.valueOf(filterStart).setScale(rounding, RoundingMode.HALF_UP).doubleValue() <=
                        BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).doubleValue() &&
                        BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).doubleValue() <=
                                BigDecimal.valueOf(filterStop).setScale(rounding, RoundingMode.HALF_UP).doubleValue();
            }

            case DATE -> {
                var current = currentVal.isEmpty() ? LocalDate.MIN : LocalDate.parse(currentVal);
                var filterStart = values.get(0).isEmpty() ? LocalDate.MIN : LocalDate.parse(values.get(0));
                var filterStop = values.get(1).isEmpty() ? LocalDate.MAX : LocalDate.parse(values.get(1));
                yield (current.isAfter(filterStart) || current.isEqual(filterStart)) && (current.isBefore(filterStop) || current.isEqual(filterStop));
            }
            case TIMESTAMP -> {
                var current = currentVal.isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(currentVal.replace(" ", "T"));
                var filterStart = values.get(0).isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(values.get(0).replace(" ", "T"));
                var filterStop = values.get(1).isEmpty() ? LocalDateTime.MAX : LocalDateTime.parse(values.get(1).replace(" ", "T"));
                yield (current.isAfter(filterStart) || current.isEqual(filterStart)) && (current.isBefore(filterStop) || current.isEqual(filterStop));
            }

            case STRING, BOOLEAN, UNKNOWN -> throw new InvalidParametersException("Not supported datatype metric");
        };

        return invert != result;
    }
}
