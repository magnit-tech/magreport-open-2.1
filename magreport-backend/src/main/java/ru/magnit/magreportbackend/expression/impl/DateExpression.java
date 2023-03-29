package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;

public class DateExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.DATE);

    public DateExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var yearParameter = parameters.get(0);
        final var yearResult = yearParameter.calculate(rowNumber);

        checkParameterNotNull(yearParameter, yearResult);
        checkParameterHasAnyType(yearParameter, yearResult, DataTypeEnum.INTEGER, DataTypeEnum.STRING);

        final var monthParameter = parameters.get(1);
        final var monthResult = monthParameter.calculate(rowNumber);

        checkParameterNotNull(monthParameter, monthResult);
        checkParameterHasAnyType(monthParameter, monthResult, DataTypeEnum.INTEGER, DataTypeEnum.STRING);

        final var dayParameter = parameters.get(2);
        final var dayResult = dayParameter.calculate(rowNumber);

        checkParameterNotNull(dayParameter, dayResult);
        checkParameterHasAnyType(dayParameter, dayResult, DataTypeEnum.INTEGER, DataTypeEnum.STRING);

        final var yearValue = Integer.parseInt(yearResult.getL());
        final var monthValue = Integer.parseInt(monthResult.getL());
        final var dayValue = Integer.parseInt(dayResult.getL());

        return result.setL(LocalDate.of(yearValue, monthValue, dayValue).toString());
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
