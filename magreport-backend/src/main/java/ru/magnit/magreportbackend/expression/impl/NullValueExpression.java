package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class NullValueExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public NullValueExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameter = parameters.get(0);
        final var value = parameter.calculate(rowNumber);

        return result.setR(DataTypeEnum.valueOf(value.getL().toUpperCase()));
    }

    @Override
    public DataTypeEnum inferType() {
        final var parameter = parameters.get(0);
        final var value = parameter.calculate(0);

        return DataTypeEnum.valueOf(value.getL().toUpperCase());
    }
}
