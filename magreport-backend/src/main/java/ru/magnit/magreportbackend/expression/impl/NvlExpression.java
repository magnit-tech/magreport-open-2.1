package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class NvlExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public NvlExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);

    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceValue = parameters.get(0).calculate(rowNumber);
        final var targetValue = parameters.get(1).calculate(rowNumber);

        checkParameterNotNull(parameters.get(1), targetValue);

        return result
            .setL(sourceValue.getL() == null ? targetValue.getL() : sourceValue.getL())
            .setR(sourceValue.getR());
    }
}
