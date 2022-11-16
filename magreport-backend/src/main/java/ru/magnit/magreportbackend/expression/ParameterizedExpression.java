package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ParameterizedExpression implements BaseExpression {
    protected final List<BaseExpression> parameters = new ArrayList<>();

    protected ParameterizedExpression(FieldExpressionResponse fieldExpression, Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes, String[][] resultCube) {
        for (var parameter : fieldExpression.getParameters()) {
            parameters.add(parameter.getType().init(parameter, fieldIndexes, resultCube));
        }
    }
}
