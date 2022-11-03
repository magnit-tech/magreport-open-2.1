package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;

public class CountFunction implements MetricsFunction {

    private int numValues;

    @Override
    public void addValue(String value, int col, int row) {
        numValues++;
    }

    @Override
    public String calculate(int col, int row) {
        return Integer.toString(numValues).intern();
    }
}
