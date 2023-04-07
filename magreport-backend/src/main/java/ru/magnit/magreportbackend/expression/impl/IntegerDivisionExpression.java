package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class IntegerDivisionExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public IntegerDivisionExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstParameter = parameters.get(0);
        final var firstValue = firstParameter.calculate(rowNumber);
        final var secondParameter = parameters.get(1);
        final var secondValue = secondParameter.calculate(rowNumber);

        if (firstValue.getL() == null || secondValue.getL() == null) {
            return result
                    .setL(null)
                    .setR(firstValue.getR().widerNumeric(secondValue.getR()));
        }

        if (firstValue.getR() == DataTypeEnum.INTEGER && secondValue.getR() == DataTypeEnum.INTEGER) {
            final var firstResult = Integer.parseInt(firstValue.getL());
            final var secondResult = Integer.parseInt(secondValue.getL());
            result
                    .setL(String.valueOf(firstResult / secondResult))
                    .setR(DataTypeEnum.INTEGER);
        } else {
            final var firstResult = Double.parseDouble(firstValue.getL());
            final var secondResult = Double.parseDouble(secondValue.getL());
            result
                    .setL(String.valueOf(Math.floor(firstResult / secondResult)))
                    .setR(DataTypeEnum.DOUBLE);
        }

        return result;
    }
}
