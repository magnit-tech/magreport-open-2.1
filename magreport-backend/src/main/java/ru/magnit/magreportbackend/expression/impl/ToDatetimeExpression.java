package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class ToDatetimeExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.TIMESTAMP);

    public ToDatetimeExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var dateParameter = parameters.get(0);
        final var dateResult = dateParameter.calculate(rowNumber);
        var timePart = " 00:00:00";

        if (Objects.isNull(dateResult.getL())) return result;


        if (parameters.size() == 2) {
            final var timeResult = parameters.get(1).calculate(rowNumber);
            checkParameterNotNull(parameters.get(1), timeResult);
            timePart = " " + timeResult.getL();
        }


        checkParameterHasAnyType(dateParameter, dateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        return result
                .setL(dateResult.getR() == DataTypeEnum.TIMESTAMP ?
                        dateResult.getL().substring(0, 10) + timePart :
                        dateResult.getL() + timePart);
    }

    @Override
    public DataTypeEnum inferType() {
        final var dateParameter = parameters.get(0);
        final var dateType = dateParameter.inferType();

        checkParameterHasAnyType(dateParameter, dateType, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);
        return result.getR();
    }
}
