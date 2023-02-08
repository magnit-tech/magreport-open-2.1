package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class DivideExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result;

    public DivideExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        result = new Pair<>(null, DataTypeEnum.INTEGER);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var calcResult = 0D;
        var isNull = false;
        var resultType = DataTypeEnum.INTEGER;
        var firstValue = true;
        for (var parameter: parameters) {
            final var parameterValue = parameter.calculate(rowNumber);

            checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

            resultType = resultType.widerNumeric(parameterValue.getR());

            if (firstValue) {
                if (parameterValue.getL() != null) {
                    calcResult = Double.parseDouble(parameterValue.getL());
                } else {
                    isNull = true;
                }
                firstValue = false;
                continue;
            }

            if (parameterValue.getL() != null) {
                calcResult /= Double.parseDouble(parameterValue.getL());
            } else {
                isNull = true;
            }
        }
        return result
            .setL(isNull? null : resultType.toTypedString(calcResult))
            .setR(resultType);
    }
}
