package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Objects;

public class SplitExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, DataTypeEnum.STRING);

    public SplitExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var delimiter = parameters.get(1).calculate(rowNumber);
        final var numberItem = parameters.get(2).calculate(rowNumber);

        if (Objects.isNull(sourceString.getL())) return result;
        checkParameterHasAnyType(parameters.get(0), sourceString, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(1), delimiter);
        checkParameterHasAnyType(parameters.get(1), delimiter, DataTypeEnum.STRING);

        checkParameterNotNull(parameters.get(2), numberItem);
        checkParameterHasAnyType(parameters.get(2), numberItem, DataTypeEnum.INTEGER);

        var splitResult = sourceString.getL().split(delimiter.getL());
        var numItem =  Integer.parseInt(numberItem.getL());

        if (splitResult.length > numItem)
            result.setL(splitResult[numItem]);

        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public DataTypeEnum inferType() {
        final var srcParam = parameters.get(0);
        final var delimiterParam = parameters.get(1);
        final var numItemParam = parameters.get(2);
        final var srcParamType = srcParam.inferType();
        final var delimiterParamType = delimiterParam.inferType();
        final var numItemParamType = numItemParam.inferType();

        checkParameterHasAnyType(srcParam, srcParamType, DataTypeEnum.STRING);
        checkParameterHasAnyType(delimiterParam, delimiterParamType, DataTypeEnum.STRING);
        checkParameterHasAnyType(numItemParam, numItemParamType, DataTypeEnum.INTEGER);

        return result.getR();
    }
}
