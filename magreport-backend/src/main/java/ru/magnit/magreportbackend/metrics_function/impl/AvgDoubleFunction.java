package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class AvgDoubleFunction implements MetricsFunction {

    private int numValues;
    private double sumOfValues;

    @Override
    public void addValue(String value, int col, int row) {

        if (value != null) {
            numValues++;
            sumOfValues = sumOfValues + Double.parseDouble(value);
        }
    }

    @Override
    public String calculate(int col, int row) {
        return String.valueOf(sumOfValues / numValues);
    }

}
