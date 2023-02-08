package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class ParameterizedExpression extends BaseExpression {
    protected final List<BaseExpression> parameters = new ArrayList<>();

    protected ParameterizedExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        this.expressionName = fieldExpression.getType().name();
        for (var parameter : fieldExpression.getParameters()) {
            final var parameterExpression = parameter.getType().init(parameter, context);
            parameterExpression.parentExpression = this;
            parameters.add(parameterExpression);
        }
    }

    @Override
    public DataTypeEnum inferType() {
        var resultType = DataTypeEnum.INTEGER;
        for (var parameter: parameters) {
            final var parameterType = parameter.inferType();
            checkParameterHasAnyType(parameter, parameterType, DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE);
            resultType = parameterType.widerNumeric(parameterType);
        }
        return resultType;
    }

    @Override
    public String getErrorPath(BaseExpression faultyExpression) {
        final var result = new StringBuilder();
        if (this == faultyExpression) result.append("<error>");
        result.append(this).append("(");
        var counter = 0;
        for (final var parameter: parameters) {
            result.append(parameter.getErrorPath(faultyExpression));
            if (counter++ < parameters.size() - 1) result.append(", ");
        }
        result.append(")");
        if (this == faultyExpression) result.append("</error>");
        return result.toString();
    }
}
