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

public class MetricGreaterOrEqualsFilter implements MetricFilterNode {

    private boolean invert;
    private String value;
    private MetricResult[][][] data;
    private int metric;
    private DataTypeEnum type;
    private int rounding;

    @Override
    public MetricFilterNode init(MetricResult[][][] metricResults, MetricFilterDefinition filterDefinition, List<DataTypeEnum> metricsDataTypes) {

        value = filterDefinition.getValues().isEmpty() ? "" : filterDefinition.getValues().get(0);
        data = metricResults;
        metric = filterDefinition.getMetricId().intValue();
        type = metricsDataTypes.get(filterDefinition.getMetricId().intValue());
        invert = filterDefinition.isInvertResult();
        rounding = filterDefinition.getRounding();
        return this;
    }

    @Override
    public boolean filter(int column, int row) {
        var currentVal = data[column][row][metric].getValue() == null ? "" : data[column][row][metric].getValue();

        var result = switch (type) {
            case INTEGER, DOUBLE -> {

                var current = currentVal.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(currentVal);
                var filter = value.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(value);

                yield BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).doubleValue() >=
                        BigDecimal.valueOf(filter).setScale(rounding, RoundingMode.HALF_UP).doubleValue();
            }

            case DATE -> {
                var current = currentVal.isEmpty() ? LocalDate.MIN : LocalDate.parse(currentVal);
                var filter = value.isEmpty() ? LocalDate.MIN : LocalDate.parse(value);
                yield current.isAfter(filter) || current.isEqual(filter);
            }
            case TIMESTAMP -> {
                var current = currentVal.isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(currentVal.replace(" ", "T"));
                var filter = value.isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(value.replace(" ", "T"));
                yield current.isAfter(filter) || current.isEqual(filter);
            }
            case STRING, BOOLEAN -> throw new InvalidParametersException("Not supported datatype metric");
        };

        return invert != (result);
    }
}
