package ru.magnit.magreportbackend.service.domain.metricsfilter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.MetricResult;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterDefinition;
import ru.magnit.magreportbackend.dto.request.olap.MetricFilterGroup;

import java.util.List;


public class MetricFilters {

    public static MetricFilterNode createFilter(MetricResult[][][] metricResults, MetricFilterGroup filterGroup, List<DataTypeEnum> metricsDataTypes) {
        if (filterGroup == null) return new EmptyFilter();
        return new MetricResultFilterGroup().init(metricResults, filterGroup, metricsDataTypes);
    }

    public static MetricFilterNode createFilter(MetricResult[][][] metricResults, MetricFilterDefinition filterDefinition, List<DataTypeEnum> metricsDataTypes) {
        if (filterDefinition == null) return new EmptyFilter();

        return switch (filterDefinition.getFilterType()) {
            case EMPTY -> new EmptyFilter();
            case EQUALS -> new MetricEqualsFilter().init(metricResults, filterDefinition, metricsDataTypes);
            case GREATER -> new MetricGreaterFilter().init(metricResults, filterDefinition, metricsDataTypes);
            case LESSER -> new MetricLesserFilter().init(metricResults, filterDefinition, metricsDataTypes);
            case GREATER_OR_EQUALS -> new MetricGreaterOrEqualsFilter().init(metricResults, filterDefinition, metricsDataTypes);
            case LESSER_OR_EQUALS -> new MetricLesserOrEqualsFilter().init(metricResults, filterDefinition, metricsDataTypes);
            case BETWEEN -> new MetricBetweenFilter().init(metricResults, filterDefinition, metricsDataTypes);
            case BLANK -> new MetricBlankFilter().init(metricResults, filterDefinition, metricsDataTypes);
        };
    }

}
