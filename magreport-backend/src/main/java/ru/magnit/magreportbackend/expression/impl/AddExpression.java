package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class AddExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>();

    public AddExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        var calcResult = 0D;
        var resultType = DataTypeEnum.INTEGER;
        for (var parameter: parameters) {
            final var parameterValue = parameter.calculate(rowNumber);

            checkParameterNotNull(parameter, parameterValue);
            checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);

            resultType = resultType.widerNumeric(parameterValue.getR());
            final var paramResult = parameter.calculate(rowNumber).getL();
            if (paramResult != null) {
                calcResult += Double.parseDouble(paramResult);
            }
        }
        return result
            .setL(resultType.toTypedString(calcResult))
            .setR(resultType);
    }
}
