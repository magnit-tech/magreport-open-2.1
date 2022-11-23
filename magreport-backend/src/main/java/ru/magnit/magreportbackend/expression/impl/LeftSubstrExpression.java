package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class LeftSubstrExpression extends ParameterizedExpression {

    private final Pair<String, DataTypeEnum> result;

    @SuppressWarnings("unused")
    public LeftSubstrExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        result = new Pair<>(null, DataTypeEnum.STRING);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var length = parameters.get(1).calculate(rowNumber);

        checkParameterNotNull(parameters.get(0), sourceString);
        checkParameterHasAnyType(parameters.get(0), sourceString, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        checkParameterNotNull(parameters.get(1), length);
        checkParameterHasAnyType(parameters.get(1), length, DataTypeEnum.INTEGER);

        return result.setL(sourceString.getL().substring(0, Integer.parseInt(length.getL())));
    }
}
