package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class LeftSubstrExpression extends ParameterizedExpression {
    public LeftSubstrExpression(FieldExpressionResponse fieldExpression) {
        super(fieldExpression);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber).getL();
        final var length = Integer.parseInt(parameters.get(1).calculate(rowNumber).getL());

        return new Pair<>(sourceString.substring(0, length), DataTypeEnum.STRING);
    }
}
