package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

public abstract class AggregateExpression extends BaseExpression {

    protected final int fieldIndex;
    protected final String[][] cubeValues;
    protected AggregateExpression(FieldExpressionResponse expression, ExpressionCreationContext context) {
        super(expression, context);
        fieldIndex = context.fieldIndexes().get(new FieldDefinition(expression.getReferenceId(), expression.getFieldType())).getL();
        cubeValues = context.resultCube();
    }
}
