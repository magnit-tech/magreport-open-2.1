package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.List;

public class IifExpression  extends ParameterizedExpression {
    public IifExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var firstParameter = parameters.get(0);
        final var condition = firstParameter.calculate(rowNumber);

        checkParameterNotNull(parameters.get(0), condition);
        checkParameterHasAnyType(firstParameter, condition.getR(), DataTypeEnum.BOOLEAN);

        final var index = condition.getL().equalsIgnoreCase("true") ? 1 : 2;

        final var resultExpression = parameters.get(index);

        return resultExpression.calculate(rowNumber);
    }

    @Override
    public DataTypeEnum inferType() {
        final var firstParameter = parameters.get(0);
        final var firstParameterType = firstParameter.inferType();
        checkParameterHasAnyType(firstParameter, firstParameterType, DataTypeEnum.BOOLEAN);
        final var secondParameter = parameters.get(0);
        final var secondParameterType = secondParameter.inferType();
        final var thirdParameter = parameters.get(0);
        final var thirdParameterType = thirdParameter.inferType();
        checkParametersHasSameTypes(this, List.of(secondParameterType, thirdParameterType));

        return secondParameterType;
    }
}
