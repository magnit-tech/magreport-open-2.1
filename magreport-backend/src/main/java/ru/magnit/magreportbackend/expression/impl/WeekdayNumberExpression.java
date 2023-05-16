package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WeekdayNumberExpression  extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.INTEGER);

    public WeekdayNumberExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstDateParameter = parameters.get(0);
        final var firstDateResult = firstDateParameter.calculate(rowNumber);

        checkParameterNotNull(firstDateParameter, firstDateResult);
        checkParameterHasAnyType(firstDateParameter, firstDateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        final var date = firstDateResult.getR() == DataTypeEnum.TIMESTAMP ?
                LocalDateTime.parse(firstDateResult.getL().replace(" ", "T")).toLocalDate() :
                LocalDate.parse(firstDateResult.getL());

        return result.setL(String.valueOf(date.getDayOfWeek().getValue()));
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
