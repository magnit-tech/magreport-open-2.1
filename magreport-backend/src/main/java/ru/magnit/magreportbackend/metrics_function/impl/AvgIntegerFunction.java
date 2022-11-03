package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class AvgIntegerFunction implements MetricsFunction {

    private int numValues;
    private int sumOfValues;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null) {
            numValues++;
            sumOfValues += Double.parseDouble(value);
        }
    }

    @Override
    public String calculate(int col, int row) {
        return (sumOfValues == 0) ? "0" : String.valueOf(sumOfValues / numValues);
    }
}
