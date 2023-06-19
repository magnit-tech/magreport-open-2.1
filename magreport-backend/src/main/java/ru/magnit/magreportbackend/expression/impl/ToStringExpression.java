package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class ToStringExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public ToStringExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameter = parameters.get(0).calculate(rowNumber);
        if (Objects.isNull(parameter.getL())) return result;
        else return result.setL(parameter.getL());
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
