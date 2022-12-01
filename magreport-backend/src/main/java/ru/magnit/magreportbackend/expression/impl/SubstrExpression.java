package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class SubstrExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public SubstrExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var startPosition = parameters.get(1).calculate(rowNumber);
        final var length = parameters.get(2).calculate(rowNumber);

        checkParameterNotNull(parameters.get(0), sourceString);
        checkParameterHasAnyType(parameters.get(0), sourceString, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(1), startPosition);
        checkParameterHasAnyType(parameters.get(1), startPosition, DataTypeEnum.INTEGER);

        checkParameterNotNull(parameters.get(2), length);
        checkParameterHasAnyType(parameters.get(2), length, DataTypeEnum.INTEGER);

        return result.setL(
            sourceString.getL().substring(
                Integer.parseInt(startPosition.getL()),
                Integer.parseInt(startPosition.getL()) + Integer.parseInt(length.getL())
            ));
    }
}
