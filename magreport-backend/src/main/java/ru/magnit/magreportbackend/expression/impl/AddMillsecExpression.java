package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AddMillsecExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.TIMESTAMP);

    public AddMillsecExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var dateParameter = parameters.get(0);
        final var dateResult = dateParameter.calculate(rowNumber);

        checkParameterNotNull(dateParameter, dateResult);
        checkParameterHasAnyType(dateParameter, dateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        final var millisParameter = parameters.get(1);
        final var millisResult = millisParameter.calculate(rowNumber);

        checkParameterNotNull(millisParameter, millisResult);
        checkParameterHasAnyType(millisParameter, millisResult, DataTypeEnum.INTEGER);

        final var millisValue = Long.parseLong(millisResult.getL());
        final var dateValue = dateResult.getR() == DataTypeEnum.TIMESTAMP ?
            LocalDateTime.parse(dateResult.getL()).plus(millisValue, ChronoUnit.MILLIS).toString() :
            LocalDate.parse(dateResult.getL()).atStartOfDay().plus(millisValue, ChronoUnit.MILLIS).toString();

        return result.setL(dateValue);
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
