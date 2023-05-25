package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class CeilExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.INTEGER);

    public CeilExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstParameter = parameters.get(0);
        final var firstValue = firstParameter.calculate(rowNumber);

        checkParameterHasAnyType(firstParameter, firstValue, DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER);

        if (firstValue.getR() == DataTypeEnum.DOUBLE) {
            final var value = Double.parseDouble(firstValue.getL());
            result.setL(String.valueOf((long) Math.ceil(value)));
        } else {
            result.setL(firstValue.getL());
        }

        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
