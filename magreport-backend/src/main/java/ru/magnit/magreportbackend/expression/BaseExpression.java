package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
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

    protected void checkParameterNotNull(BaseExpression parameter, Pair<String, DataTypeEnum> parameterValue) {
        if (parameterValue.getL() == null) {
            throw new InvalidExpression(ExpressionExceptionUtils.getParameterIsNullMessage(getRootExpression().getErrorPath(parameter), derivedField, expressionName));
        }
    }

    protected void checkParameterHasAnyType(BaseExpression parameter, Pair<String, DataTypeEnum> parameterValue, DataTypeEnum... types){
        if (parameterValue.getR().notIn(types)){
            throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypeMessage(getRootExpression().getErrorPath(parameter), derivedField, expressionName, parameterValue.getR().name()));
        }
    }
}
