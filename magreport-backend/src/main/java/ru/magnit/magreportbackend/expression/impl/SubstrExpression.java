package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class SubstrExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public SubstrExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var startPosition = parameters.get(1).calculate(rowNumber);
        final var length = parameters.get(2).calculate(rowNumber);

        if (Objects.isNull(sourceString.getL())) return result;
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

    @SuppressWarnings("DuplicatedCode")
    @Override
    public DataTypeEnum inferType() {
        final var srcParam = parameters.get(0);
        final var startParam = parameters.get(1);
        final var lenParam = parameters.get(2);
        final var srcParamType = srcParam.inferType();
        final var startParamType = startParam.inferType();
        final var lenParamType = lenParam.inferType();

        checkParameterHasAnyType(srcParam, srcParamType, DataTypeEnum.STRING);
        checkParameterHasAnyType(startParam, startParamType, DataTypeEnum.INTEGER);
        checkParameterHasAnyType(lenParam, lenParamType, DataTypeEnum.INTEGER);

        return result.getR();
    }
}
