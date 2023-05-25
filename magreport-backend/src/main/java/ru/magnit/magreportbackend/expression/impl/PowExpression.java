package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class PowExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.DOUBLE);

    public PowExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameterValue1 = parameters.get(0).calculate(rowNumber);
        final var parameter1 = parameters.get(0);

        final var parameterValue2 = parameters.get(1).calculate(rowNumber);
        final var parameter2 = parameters.get(1);

        checkParameterHasAnyType(parameter1, parameterValue1, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

        checkParameterHasAnyType(parameter2, parameterValue2, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

        final var isNull = parameterValue1.getL() == null || parameterValue2.getL() == null;
        result.setL(isNull? null : String.valueOf(Math.pow(Double.parseDouble(parameterValue1.getL()), Double.parseDouble(parameterValue2.getL()))));

        return result;
    }
}
