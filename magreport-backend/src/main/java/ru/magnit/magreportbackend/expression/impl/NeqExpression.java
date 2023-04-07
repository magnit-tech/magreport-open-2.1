package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.List;

import static ru.magnit.magreportbackend.util.Constant.FALSE;
import static ru.magnit.magreportbackend.util.Constant.TRUE;

public class NeqExpression  extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.BOOLEAN);

    public NeqExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstParameter = parameters.get(0);
        final var firstValue = firstParameter.calculate(rowNumber);
        final var secondParameter = parameters.get(1);
        final var secondValue = secondParameter.calculate(rowNumber);

        checkParametersHasSameTypes(this, List.of(firstValue.getR(), secondValue.getR()));

        if (!firstValue.getL().equalsIgnoreCase(secondValue.getL())) {
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
