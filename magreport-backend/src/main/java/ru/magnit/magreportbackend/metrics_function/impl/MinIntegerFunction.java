package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class MinIntegerFunction implements MetricsFunction {

    private Long minValue;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null)
            minValue = minValue== null ? Long.parseLong(value) : Long.min(minValue, Long.parseLong(value));

    }

    @Override
    public String calculate(int col, int row) {
        return minValue == null ? "" : String.valueOf(minValue.longValue());
    }
}
