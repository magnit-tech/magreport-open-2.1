package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class SumIntegerFunction implements MetricsFunction {

    private int sumOfValues;

    @Override
    public void addValue(String value, int col, int row) {
        if (value != null)
            sumOfValues += Double.parseDouble(value);
    }

    @Override
    public String calculate(int col, int row) {
        return String.valueOf(sumOfValues);
    }
}
