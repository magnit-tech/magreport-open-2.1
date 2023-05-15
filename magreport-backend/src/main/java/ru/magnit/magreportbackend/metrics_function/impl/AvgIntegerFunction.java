package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class AvgIntegerFunction implements MetricsFunction {

    private long numValues;
    private long sumOfValues;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null) {
            numValues++;
            sumOfValues += Long.parseLong(value);
        }
    }

    @Override
    public String calculate(int col, int row) {
        return (sumOfValues == 0) ? "0" : String.valueOf(sumOfValues / numValues);
    }
}
