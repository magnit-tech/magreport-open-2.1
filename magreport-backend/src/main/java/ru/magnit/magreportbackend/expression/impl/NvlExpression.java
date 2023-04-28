package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ExpressionExceptionUtils;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class NvlExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public NvlExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);

    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceValue = parameters.get(0).calculate(rowNumber);
        final var targetValue = parameters.get(1).calculate(rowNumber);

        checkParameterNotNull(parameters.get(1), targetValue);

        return result
                .setR(sourceValue.getL() == null ? targetValue.getR() : sourceValue.getR())
                .setL(sourceValue.getL() == null ? targetValue.getL() : sourceValue.getL());
    }

    @Override
    public DataTypeEnum inferType() {
        final var sourceParameter = parameters.get(0);
        final var targetParameter = parameters.get(1);
        final var sourceType = sourceParameter.inferType();
        final var targetType = targetParameter.inferType();
        if (sourceType != targetType) {
            throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypesMessage(getRootExpression().getErrorPath(this), derivedField, expressionName, sourceType.name(), targetType.name()));
        }
        return super.inferType();
    }
}
