package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

public class LeftSubstrExpression extends ParameterizedExpression {

    private final Pair<String, DataTypeEnum> result;

    @SuppressWarnings("unused")
    public LeftSubstrExpression(FieldExpressionResponse fieldExpression, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] resultCube) {
        super(fieldExpression, fieldIndexes, resultCube);
        result = new Pair<>(null, DataTypeEnum.STRING);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceString = parameters.get(0).calculate(rowNumber);
        final var length = parameters.get(1).calculate(rowNumber);

        if (sourceString.getL() == null || length.getL() == null) {
            result.setL("");
        } else {
            result.setL(sourceString.getL().substring(0, Integer.parseInt(length.getL())));
        }
        return result;
    }
}
