package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class ParameterizedExpression implements BaseExpression {
    protected final List<BaseExpression> parameters = new ArrayList<>();

    protected ParameterizedExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        for (var parameter : fieldExpression.getParameters()) {
            parameters.add(parameter.getType().init(parameter, context));
        }
    }
}
