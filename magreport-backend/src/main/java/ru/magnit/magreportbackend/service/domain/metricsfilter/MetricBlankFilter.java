package ru.magnit.magreportbackend.service.domain.metricsfilter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterDefinition;

import java.util.List;

public class MetricBlankFilter implements MetricFilterNode{

    private boolean invert;
    private MetricResult[][][] data;
    private int metric;

    @Override
    public MetricFilterNode init(MetricResult[][][] metricResults, MetricFilterDefinition filterDefinition, List<DataTypeEnum> metricsDataTypes) {
        data = metricResults;
        invert = filterDefinition.isInvertResult();
        metric = filterDefinition.getMetricId().intValue();
        return this;
    }

    @Override
    public boolean filter(int column, int row) {
        var currentVal = data[column][row][metric].getValue() == null ? "" : data[column][row][metric].getValue();
        return invert != (currentVal.isEmpty());
    }
}
