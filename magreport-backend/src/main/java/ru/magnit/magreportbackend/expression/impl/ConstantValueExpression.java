package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

public class ConstantValueExpression implements BaseExpression {
    private final Pair<String, DataTypeEnum> value;

    @SuppressWarnings("unused")
    public ConstantValueExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        value = new Pair<>(fieldExpression.getConstantValue(), fieldExpression.getConstantType());
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        return value;
    }
}
