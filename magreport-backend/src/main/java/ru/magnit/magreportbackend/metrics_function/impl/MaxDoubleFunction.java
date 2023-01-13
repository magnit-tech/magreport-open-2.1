package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class MaxDoubleFunction implements MetricsFunction {

    private Double maxValue;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null)
            maxValue = maxValue == null ? Double.parseDouble(value) : Double.max(maxValue, Double.parseDouble(value));
    }

    @Override
    public String calculate(int col, int row) {
        return maxValue == null ? "" : String.valueOf(maxValue);
    }
}
