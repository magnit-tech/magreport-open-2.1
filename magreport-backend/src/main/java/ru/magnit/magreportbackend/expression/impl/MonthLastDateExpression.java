package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public class MonthLastDateExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.DATE);

    public MonthLastDateExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var dateParameter = parameters.get(0);
        final var dateResult = dateParameter.calculate(rowNumber);

        if (Objects.isNull(dateResult.getL())) return result;
        checkParameterHasAnyType(dateParameter, dateResult, DataTypeEnum.DATE);

        final var dateValue = LocalDate.parse(dateResult.getL());

        return result.setL(dateValue.with(TemporalAdjusters.lastDayOfMonth()).toString());
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
