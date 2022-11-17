package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

public class DerivedFieldValueExpression implements BaseExpression {
    private final Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes;
    private final FieldDefinition fieldDefinition;
    private final String[][] resultCube;
    private final Pair<String, DataTypeEnum> result;

    public DerivedFieldValueExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        this.fieldIndexes = context.fieldIndexes();
        fieldDefinition = new FieldDefinition(fieldExpression.getReferenceId(), OlapFieldTypes.DERIVED_FIELD);
        this.resultCube = context.resultCube();
        /// !!! Для сокращения генерации мусора, не запускать параллельно
        this.result = new Pair<>(null, null);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        return result
            .setL(resultCube[fieldIndexes.get(fieldDefinition).getL()][rowNumber])
            .setR(fieldIndexes.get(fieldDefinition).getR());
    }
}
