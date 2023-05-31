package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

public abstract class AggregateExpression extends BaseExpression {
    protected AggregateExpression(FieldExpressionResponse ignored, ExpressionCreationContext context) {
        super(ignored, context);
    }
}
