package ru.magnit.magreportbackend.metrics_function.impl;

import ru.magnit.magreportbackend.metrics_function.MetricsFunction;
import ru.magnit.magreportbackend.util.StringUtils;

public class MaxStringFunction implements MetricsFunction {

    private String maxValue;

    @Override
    public void addValue(String value, int col, int row){
        if (value != null)
            maxValue = maxValue == null ? value :  StringUtils.max(maxValue,value);
    }

    @Override
    public String calculate(int col, int row) {
        return maxValue;
    }
}
