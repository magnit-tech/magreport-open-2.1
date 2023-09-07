package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class SubtractExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public SubtractExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings({"DuplicatedCode", "java:S135"})
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var calcResult = 0D;
        var resultType = DataTypeEnum.INTEGER;
        var firstValue = true;
        var isNull = false;
        for (var parameter : parameters) {
            final var parameterValue = parameter.calculate(rowNumber);

            checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

            resultType = resultType.widerNumeric(parameterValue.getR());

            if (parameterValue.getL() == null) {
                isNull = true;
                break;
            }

            if (firstValue) {
                calcResult = Double.parseDouble(parameterValue.getL());
                firstValue = false;
                continue;
            }

            calcResult -= Double.parseDouble(parameterValue.getL());
        }
        return result
            .setL(isNull ? null : resultType.toTypedString(calcResult))
            .setR(resultType);
    }
}
