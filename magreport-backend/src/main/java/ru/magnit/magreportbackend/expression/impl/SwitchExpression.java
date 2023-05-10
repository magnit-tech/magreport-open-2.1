package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

public class SwitchExpression extends ParameterizedExpression {
    public SwitchExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var numParams = parameters.size();

        checkParametersCountIsOdd(this, numParams);

        for (int i = 0; i < numParams - 1; i += 2) {
            final var predicateParameter = parameters.get(i);
            final var predicateValue = predicateParameter.calculate(rowNumber);

            checkParameterHasAnyType(predicateParameter, predicateValue, DataTypeEnum.BOOLEAN);

            if (predicateValue.getL().equals("true")) {
                return parameters.get(i+1).calculate(rowNumber);
            }
        }

        return parameters.get(numParams - 1).calculate(rowNumber);
    }

    @Override
    public DataTypeEnum inferType() {
        final var parameter = parameters.get(1);

        return parameter.inferType();
    }
}
