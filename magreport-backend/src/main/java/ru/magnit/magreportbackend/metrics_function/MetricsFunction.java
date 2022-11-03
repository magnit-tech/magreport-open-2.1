package ru.magnit.magreportbackend.metrics_function;

public interface MetricsFunction {
    void addValue(String value, int col, int row);
    String calculate(int col, int row);
}
