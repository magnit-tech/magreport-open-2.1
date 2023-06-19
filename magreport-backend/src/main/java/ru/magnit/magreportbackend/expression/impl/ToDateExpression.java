package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class ToDateExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.DATE);

    public ToDateExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var dateParameter = parameters.get(0);
        final var dateResult = dateParameter.calculate(rowNumber);

        if (Objects.isNull(dateResult.getL())) return result;
        checkParameterHasAnyType(dateParameter, dateResult, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);

        return result
                .setL(dateResult.getR() == DataTypeEnum.DATE ?
                        dateResult.getL() :
                        dateResult.getL().substring(0, 10));
    }

    @Override
    public DataTypeEnum inferType() {
        final var dateParameter = parameters.get(0);
        final var dateType = dateParameter.inferType();

        checkParameterHasAnyType(dateParameter, dateType, DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP);
        return result.getR();
    }
}
