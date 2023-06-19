package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class LeftSubstrExpression extends ParameterizedExpression {

    private final Pair<String, DataTypeEnum> result;

    public LeftSubstrExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        result = new Pair<>(null, DataTypeEnum.STRING);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var length = parameters.get(1).calculate(rowNumber);

        if (Objects.isNull(sourceString.getL())) return result;
        final var stringLength = sourceString.getL().length();

        checkParameterHasAnyType(parameters.get(0), sourceString, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        checkParameterNotNull(parameters.get(1), length);
        checkParameterHasAnyType(parameters.get(1), length, DataTypeEnum.INTEGER);
        final var paramLength = Integer.parseInt(length.getL());
        return result.setL(sourceString.getL().substring(0, Math.min(stringLength, paramLength)));
    }

    @Override
    public DataTypeEnum inferType() {
        final var stringParameter = parameters.get(0);
        final var lengthParameter = parameters.get(1);
        final var stringParameterType = stringParameter.inferType();
        final var lengthParameterType = stringParameter.inferType();
        checkParameterHasAnyType(stringParameter, stringParameterType, DataTypeEnum.STRING, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);
        checkParameterHasAnyType(lengthParameter, lengthParameterType, DataTypeEnum.INTEGER);
        return DataTypeEnum.STRING;
    }
}
