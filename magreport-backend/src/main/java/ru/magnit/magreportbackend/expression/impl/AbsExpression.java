package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class AbsExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public AbsExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameter = parameters.get(0);
        final var parameterValue = parameter.calculate(rowNumber);

        if (Objects.isNull(parameterValue.getL())) return result;
        checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

        result.setR(parameterValue.getR());

        if (parameterValue.getR() == DataTypeEnum.INTEGER) {
            result.setL(String.valueOf(Math.abs(Integer.parseInt(parameterValue.getL()))));
        } else {
            result.setL(String.valueOf(Math.abs(Double.parseDouble(parameterValue.getL()))));
        }

        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        final var parameter = parameters.get(0);
        final var parameterType = parameter.inferType();
        checkParameterHasAnyType(parameter, parameterType, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

        return parameterType;
    }
}
