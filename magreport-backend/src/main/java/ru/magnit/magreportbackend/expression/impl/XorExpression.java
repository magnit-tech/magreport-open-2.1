package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import static ru.magnit.magreportbackend.util.Constant.FALSE;
import static ru.magnit.magreportbackend.util.Constant.TRUE;

public class XorExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.BOOLEAN);

    public XorExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstParameter = parameters.get(0);
        final var firstValue = firstParameter.calculate(rowNumber);
        final var secondParameter = parameters.get(1);
        final var secondValue = secondParameter.calculate(rowNumber);

        checkParameterHasAnyType(firstParameter, firstValue, DataTypeEnum.BOOLEAN);
        checkParameterHasAnyType(secondParameter, secondValue, DataTypeEnum.BOOLEAN);

        final var val1 = firstValue.getL().equals(TRUE);
        final var val2 = secondValue.getL().equals(TRUE);

        if (val1 ^ val2) {
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
