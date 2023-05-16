package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class MaxIntegerFunction implements MetricsFunction {

    private Long maxValue;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null)
            maxValue = maxValue == null ? Long.parseLong(value) : Long.max(maxValue, Long.parseLong(value));
    }

    @Override
    public String calculate(int col, int row) {
        return maxValue == null ? "" : String.valueOf(maxValue.longValue());
    }
}
