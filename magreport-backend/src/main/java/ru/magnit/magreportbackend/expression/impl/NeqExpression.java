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
        final var firstType = firstValue.getR();
        final var secondParameter = parameters.get(1);
        final var secondValue = secondParameter.calculate(rowNumber);
        final var secondType = secondValue.getR();

        checkParametersComparable(this, List.of(firstType, secondType));

        final var resultType = firstType.in(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE) ?
                firstType.widerNumeric(secondType) :
                firstType;

        switch (resultType) {
            case INTEGER -> {
                final var value1 = Integer.parseInt(firstValue.getL());
                final var value2 = Integer.parseInt(secondValue.getL());

                if (value1 != value2) {
                    result.setL(TRUE);
                } else {
                    result.setL(FALSE);
                }
            }
            case STRING, TIMESTAMP, DATE, BOOLEAN -> {
                if (!firstValue.getL().equalsIgnoreCase(secondValue.getL())) {
                    result.setL(TRUE);
                } else {
                    result.setL(FALSE);
                }
            }
            case DOUBLE -> {
                final var value1 = Double.parseDouble(firstValue.getL());
                final var value2 = Double.parseDouble(secondValue.getL());

                if (value1 != value2) {
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
        final var firstParameterType = parameters.get(0).inferType();
        final var secondParameterType = parameters.get(1).inferType();
        checkParametersComparable(this, List.of(firstParameterType, secondParameterType));

        return result.getR();
    }
}
