package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class MinDoubleFunction implements MetricsFunction {

    private Double minValue;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null)
            minValue = minValue == null ? Double.parseDouble(value) : Double.min(minValue, Double.parseDouble(value));
    }

    @Override
    public String calculate(int col, int row) {
        return minValue == null ? "" : String.valueOf(minValue);
    }
}
