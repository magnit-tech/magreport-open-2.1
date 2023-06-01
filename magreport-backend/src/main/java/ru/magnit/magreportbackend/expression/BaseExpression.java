package ru.magnit.magreportbackend.expression;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.util.List;

public abstract class BaseExpression {
    protected BaseExpression parentExpression;
    protected String expressionName;
    protected DerivedFieldResponse derivedField;

    @SuppressWarnings("unused")
    protected BaseExpression(FieldExpressionResponse ignored, ExpressionCreationContext context) {
        this.derivedField = context.derivedField();
    }

    public void addValue(int cubeRow, int rowNumber, int columnNumber){

    }

    public abstract Pair<String, DataTypeEnum> calculate(int rowNumber);

    public abstract DataTypeEnum inferType();

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

    protected void checkParameterHasAnyType(BaseExpression parameter, Pair<String, DataTypeEnum> parameterValue, DataTypeEnum... types) {
        if (parameterValue.getR().notIn(types)) {
            throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypeMessage(getRootExpression().getErrorPath(parameter), derivedField, expressionName, parameterValue.getR().name()));
        }
    }

    protected void checkParameterHasAnyType(BaseExpression parameter, DataTypeEnum parameterType, DataTypeEnum... types) {
        if (parameterType.notIn(types)) {
            throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypeMessage(getRootExpression().getErrorPath(parameter), derivedField, expressionName, parameterType.name()));
        }
    }

    protected void checkParametersCountIsOdd(BaseExpression parameter, int parametersCount) {
        if (parametersCount % 2 != 1) {
            throw new InvalidExpression(ExpressionExceptionUtils.getNumberOfParametersMustBeOdd(getRootExpression().getErrorPath(parameter), derivedField, expressionName));
        }
    }

    protected void checkParametersHasSameTypes(BaseExpression currentExpression, List<DataTypeEnum> parameterTypes) {
        if (parameterTypes.isEmpty()) return;
        DataTypeEnum parameterType = null;

        for (final var currentType : parameterTypes) {
            if (parameterType == null) {
                parameterType = currentType;
                continue;
            }
            if (currentType != parameterType) {
                throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypesMessage(getRootExpression().getErrorPath(currentExpression), derivedField, expressionName, currentType.name(), parameterType.name()));
            }
        }
    }

    protected void checkParametersComparable(BaseExpression currentExpression, List<DataTypeEnum> parameterTypes) {
        if (parameterTypes.isEmpty()) return;
        DataTypeEnum parameterType = null;

        for (final var currentType : parameterTypes) {
            if (parameterType == null) {
                parameterType = currentType;
                continue;
            }
            if (currentType != parameterType &&
                    currentType.notIn(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE) &&
                    parameterType.notIn(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE)) {
                throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypesMessage(getRootExpression().getErrorPath(currentExpression), derivedField, expressionName, currentType.name(), parameterType.name()));
            }
        }
    }
}
