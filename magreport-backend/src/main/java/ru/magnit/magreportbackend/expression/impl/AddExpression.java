package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AddExpression implements BaseExpression {
    private final List<BaseExpression> parameters = new ArrayList<>();

    public AddExpression(FieldExpressionResponse fieldExpression) {
        for (var parameter: fieldExpression.getParameters()) {
            parameters.add(parameter.getType().init(parameter));
        }
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var result = 0D;
        for (var parameter: parameters) {
            result += Double.parseDouble(parameter.calculate(rowNumber).getL());
        }
        return new Pair<>(String.valueOf(result), DataTypeEnum.DOUBLE);
    }
}
