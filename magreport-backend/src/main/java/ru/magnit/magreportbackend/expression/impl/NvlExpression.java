package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.expression.ExpressionExceptionUtils;
import ru.magnit.magreportbackend.expression.ParameterizedExpression;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class NvlExpression extends ParameterizedExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(null, null);

    public NvlExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);

    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        final var sourceValue = parameters.get(0).calculate(rowNumber);
        final var targetValue = parameters.get(1).calculate(rowNumber);

        checkParameterNotNull(parameters.get(1), targetValue);

        final var sourceType = sourceValue.getR();
        final var targetType = targetValue.getR();

        if (sourceType.in(DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP) && targetType.in(DataTypeEnum.DATE, DataTypeEnum.TIMESTAMP)) {
            result.setR(DataTypeEnum.TIMESTAMP.in(sourceType, targetType) ? DataTypeEnum.TIMESTAMP : DataTypeEnum.DATE);
            result.setL(sourceValue.getL() == null ?
                    expandDateTime(targetValue.getL(), targetType, result.getR()) :
                    expandDateTime(sourceValue.getL(), sourceType, result.getR()));
        } else if (sourceType.in(DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER) && targetType.in(DataTypeEnum.DOUBLE, DataTypeEnum.INTEGER)) {
            result.setR(DataTypeEnum.DOUBLE.in(sourceType, targetType) ? DataTypeEnum.DOUBLE : DataTypeEnum.INTEGER);
            result.setL(sourceValue.getL() == null ? targetValue.getL() : sourceValue.getL());
        } else {
            checkParametersHasSameTypes(this, List.of(sourceType, targetType));
            result
                    .setL(sourceValue.getL() == null ? targetValue.getL() : sourceValue.getL())
                    .setR(sourceType);
        }

        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        final var sourceParameter = parameters.get(0);
        final var targetParameter = parameters.get(1);
        final var sourceType = sourceParameter.inferType();
        final var targetType = targetParameter.inferType();
        if (sourceType != targetType) {
            throw new InvalidExpression(ExpressionExceptionUtils.getWrongParameterTypesMessage(getRootExpression().getErrorPath(this), derivedField, expressionName, sourceType.name(), targetType.name()));
        }
        return sourceType;
    }

    private String expandDateTime(String value, DataTypeEnum valueType, DataTypeEnum targetType) {
        if (valueType == targetType) return value;
        if (valueType == DataTypeEnum.TIMESTAMP) {
            return LocalDateTime.parse(value.replace(" ", "T")).toLocalDate().toString().replace("T", " ");
        } else {
            return LocalDate.parse(value).atStartOfDay().toString().replace("T", " ");
        }
    }
}
