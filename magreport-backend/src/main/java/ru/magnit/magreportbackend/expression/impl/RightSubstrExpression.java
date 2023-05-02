package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class RightSubstrExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public RightSubstrExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var length = parameters.get(1).calculate(rowNumber);
        final var stringLength = sourceString.getL().length();

        checkParameterNotNull(parameters.get(0), sourceString);
        checkParameterHasAnyType(parameters.get(0), sourceString, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(1), length);
        checkParameterHasAnyType(parameters.get(1), length, DataTypeEnum.INTEGER);

        var paramLength = Integer.parseInt(length.getL());
        return result
            .setL(sourceString.getL().substring(stringLength - Math.min(paramLength, stringLength)));
    }

    @Override
    public DataTypeEnum inferType() {
        final var sourceParam = parameters.get(0);
        final var lengthParam = parameters.get(1);
        final var sourceParamType = sourceParam.inferType();
        final var lengthParamType = lengthParam.inferType();

        checkParameterHasAnyType(sourceParam, sourceParamType, DataTypeEnum.STRING);
        checkParameterHasAnyType(lengthParam, lengthParamType, DataTypeEnum.INTEGER);

        return sourceParamType;
    }
}
