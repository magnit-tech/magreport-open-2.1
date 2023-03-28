package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class ConcatExpression  extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public ConcatExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var value = new StringBuilder();

        for (final var parameter : parameters) {
            value.append(parameter.calculate(rowNumber).getL());
        }

        return result.setL(value.toString());
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
