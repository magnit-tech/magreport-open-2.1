package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddMonthsExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public AddMonthsExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var dateParameter = parameters.get(0);
        final var dateResult = dateParameter.calculate(rowNumber);

        checkParameterNotNull(dateParameter, dateResult);
        checkParameterHasAnyType(dateParameter, dateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        final var monthsParameter = parameters.get(1);
        final var monthsResult = monthsParameter.calculate(rowNumber);

        checkParameterNotNull(monthsParameter, monthsResult);
        checkParameterHasAnyType(monthsParameter, monthsResult, DataTypeEnum.INTEGER);

        final var monthsValue = Long.parseLong(monthsResult.getL());
        final var dateValue = dateResult.getR() == DataTypeEnum.TIMESTAMP ?
            LocalDateTime.parse(dateResult.getL()).plusMonths(monthsValue).toString() :
            LocalDate.parse(dateResult.getL()).plusMonths(monthsValue).toString();

        return result.setL(dateValue).setR(dateResult.getR());
    }

    @Override
    public DataTypeEnum inferType() {
        return parameters.get(0).inferType();
    }
}
