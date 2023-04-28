package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MillsecIntervalExtension extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.INTEGER);

    public MillsecIntervalExtension(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstDateParameter = parameters.get(0);
        final var firstDateResult = firstDateParameter.calculate(rowNumber);

        checkParameterNotNull(firstDateParameter, firstDateResult);
        checkParameterHasAnyType(firstDateParameter, firstDateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        final var secondDateParameter = parameters.get(1);
        final var secondDateResult = secondDateParameter.calculate(rowNumber);

        checkParameterNotNull(secondDateParameter, secondDateResult);
        checkParameterHasAnyType(secondDateParameter, secondDateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        final var firstDate = firstDateResult.getR() == DataTypeEnum.TIMESTAMP ?
            LocalDateTime.parse(firstDateResult.getL().replace(" ", "T")) :
            LocalDate.parse(firstDateResult.getL()).atStartOfDay();

        final var secondDate = secondDateResult.getR() == DataTypeEnum.TIMESTAMP ?
            LocalDateTime.parse(secondDateResult.getL().replace(" ", "T")) :
            LocalDate.parse(secondDateResult.getL()).atStartOfDay();

        return result.setL(String.valueOf(firstDate.until(secondDate, ChronoUnit.MILLIS)));
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
