package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.util.Pair;

public abstract class BaseExpression {
    protected BaseExpression parentExpression;
    protected String expressionName;
    protected DerivedFieldResponse derivedField;

    @SuppressWarnings("unused")
    protected BaseExpression(FieldExpressionResponse ignored, ExpressionCreationContext context) {
        this.derivedField = context.derivedField();
    }

    public abstract Pair<String, DataTypeEnum> calculate(int rowNumber);

    public BaseExpression getRootExpression() {
        if (parentExpression == null) return this;
        return parentExpression.getRootExpression();
    }

    @Override
    public String toString() {
        return expressionName;
    }

    public String getErrorPath(BaseExpression faultyExpression) {
        if (faultyExpression == this) {
            return "<error>" + this + "</error>";
        } else {
            return toString();
        }
    }
}
