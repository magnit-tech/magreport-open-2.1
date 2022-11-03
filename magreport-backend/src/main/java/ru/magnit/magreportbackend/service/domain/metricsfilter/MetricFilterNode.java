package ru.magnit.magreportbackend.service.domain.metricsfilter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterDefinition;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;

import java.util.List;

public interface MetricFilterNode {

    default MetricFilterNode init(MetricResult[][][] metricResults, MetricFilterGroup filterGroup, List<DataTypeEnum> metricsDataTypes) {return null;}
    default MetricFilterNode init(MetricResult[][][] metricResults, MetricFilterDefinition filterDefinition, List<DataTypeEnum> metricsDataTypes) {return null;}
    boolean filter(int column, int row);

}
