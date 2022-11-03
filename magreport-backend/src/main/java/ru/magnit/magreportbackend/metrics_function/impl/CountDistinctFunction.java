package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;
import ru.magnit.magreportbackend.util.Triple;

import java.util.Set;

public class CountDistinctFunction implements MetricsFunction {
    Set<Triple<Integer, Integer, String>> resultSet;
    private long numValues;

    public CountDistinctFunction(Set<Triple<Integer, Integer, String>> resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public void addValue(String value, int column, int row) {
        if (value != null && resultSet.add(new Triple<>(column, row, value))) {
            numValues++;
        }
    }

    @SuppressWarnings("unused")
    public String calculate(int column, int row) {
        return Long.toString(numValues);
    }
}
