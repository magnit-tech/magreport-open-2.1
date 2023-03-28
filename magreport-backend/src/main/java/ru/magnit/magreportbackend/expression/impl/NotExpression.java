package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import static ru.magnit.magreportbackend.util.Constant.FALSE;
import static ru.magnit.magreportbackend.util.Constant.TRUE;

public class NotExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.BOOLEAN);

    public NotExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstParameter = parameters.get(0);
        final var firstValue = firstParameter.calculate(rowNumber);

        checkParameterHasAnyType(firstParameter, firstValue, DataTypeEnum.BOOLEAN);

        final var val1 = firstValue.getL().equals(TRUE);

        if (!val1) {
            result.setL(TRUE);
        } else {
            result.setL(FALSE);
        }

        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        return parameters.get(0).inferType();
    }
}
