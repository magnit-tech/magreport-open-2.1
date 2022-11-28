package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class ReplaceExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    @SuppressWarnings("unused")
    public ReplaceExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var what = parameters.get(1).calculate(rowNumber);
        final var replacement = parameters.get(2).calculate(rowNumber);

        checkParameterNotNull(parameters.get(0), sourceString);
        checkParameterHasAnyType(parameters.get(0), sourceString, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(1), what);
        checkParameterHasAnyType(parameters.get(1), what, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(2), replacement);
        checkParameterHasAnyType(parameters.get(2), replacement, DataTypeEnum.STRING);

        return result.setL(sourceString.getL().replace(what.getL(), replacement.getL()));
    }
}
