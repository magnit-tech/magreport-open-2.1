package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

public class SubstrExpression extends ParameterizedExpression {
    @SuppressWarnings("unused")
    public SubstrExpression(FieldExpressionResponse fieldExpression, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] resultCube) {
        super(fieldExpression, fieldIndexes, resultCube);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber).getL();
        final var startPosition = Integer.parseInt(parameters.get(1).calculate(rowNumber).getL());
        final var length = Integer.parseInt(parameters.get(2).calculate(rowNumber).getL());

        return new Pair<>(sourceString.substring(startPosition, startPosition+length), DataTypeEnum.STRING);
    }
}
