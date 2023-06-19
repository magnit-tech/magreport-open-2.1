package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Objects;

public class WeekFromDateExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.INTEGER);

    public WeekFromDateExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var parameter = parameters.get(0);
        final var parameterValue = parameter.calculate(rowNumber);

        if (Objects.isNull(parameterValue.getL())) return result;

        checkParameterHasAnyType(parameter, parameterValue, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);
        final var value = LocalDate.parse(parameterValue.getL().substring(0, 10));

        return result.setL(String.valueOf(value.get(WeekFields.ISO.weekOfYear())));
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
