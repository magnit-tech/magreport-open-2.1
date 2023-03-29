package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.List;

import static ru.magnit.magreportbackend.util.Constant.FALSE;
import static ru.magnit.magreportbackend.util.Constant.TRUE;

public class LtExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.BOOLEAN);

    public LtExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
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

        switch (firstValue.getR()) {
            case INTEGER -> {
                final var value1 = Integer.parseInt(firstValue.getL());
                final var value2 = Integer.parseInt(secondValue.getL());

                if (value1 < value2) {
                    result.setL(TRUE);
                } else {
                    result.setL(FALSE);
                }
            }
            case STRING, TIMESTAMP, DATE -> {
                if (firstValue.getL().compareToIgnoreCase(secondValue.getL()) < 0) {
                    result.setL(TRUE);
                } else {
                    result.setL(FALSE);
                }
            }
            case DOUBLE -> {
                final var value1 = Double.parseDouble(firstValue.getL());
                final var value2 = Double.parseDouble(secondValue.getL());

                if (value1 < value2) {
                    result.setL(TRUE);
                } else {
                    result.setL(FALSE);
                }
            }
            case BOOLEAN -> {
                if (firstValue.getL().equals(FALSE) && secondValue.getL().equals(TRUE)){
                    result.setL(TRUE);
                } else {
                    result.setL(FALSE);
                }
            }
        }

        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        return parameters.get(0).inferType();
    }
}
