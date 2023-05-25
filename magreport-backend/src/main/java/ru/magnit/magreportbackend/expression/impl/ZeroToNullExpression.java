package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class ZeroToNullExpression  extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public ZeroToNullExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameter = parameters.get(0);
        final var parameterValue = parameter.calculate(rowNumber);

        checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

        result.setR(parameterValue.getR());

        if (parameterValue.getL() == null) {
            return result.setL(null);
        }

        if (parameterValue.getR() == DataTypeEnum.INTEGER) {
            final var parameterValueResult = Integer.parseInt(parameterValue.getL());

            if (parameterValueResult == 0) {
                result.setL(null);
            } else {
                result.setL(parameterValue.getL());
            }
        } else {
            final var parameterValueResult = Double.parseDouble(parameterValue.getL());

            if (parameterValueResult == 0) {
                result.setL(null);
            } else {
                result.setL(parameterValue.getL());
            }
        }

        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        final var parameter = parameters.get(0);

        return parameter.inferType();
    }
}
