package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class MonthFromDateExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.INTEGER);

    public MonthFromDateExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameter = parameters.get(0);
        final var parameterValue = parameter.calculate(rowNumber);

        if (Objects.isNull(parameterValue.getL())) return result;

        checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);
        final var value = Long.parseLong(parameterValue.getL().substring(5, 7));
        return result.setL(String.valueOf(value));
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
