package ru.magnit.magreportbackend.expression.agregate;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.AggregateExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

public class AvgExpression extends AggregateExpression {
    private static final String NON_NUMERIC_FIELD_EXCEPTION = "AVG function can not be applied to non numeric field.";
    private final DataTypeEnum dataType;
    private long longSumValues;
    private double doubleSumValues;
    private long numValues;

    public AvgExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        final var fieldDefinition = new FieldDefinition(fieldExpression.getReferenceId(), fieldExpression.getFieldType());
        dataType = context.fieldIndexes().get(fieldDefinition).getR();
        if (dataType.notIn(DataTypeEnum.INTEGER, DataTypeEnum.DOUBLE)) {
            throw new InvalidExpression(NON_NUMERIC_FIELD_EXCEPTION);
        }
        this.expressionName = fieldExpression.getType().name();
    }

    @Override
    public void addValue(int cubeRow, int rowNumber, int columnNumber) {
        final var value = cubeValues[fieldIndex][cubeRow];
        if (value != null) {
            numValues++;
            switch (dataType) {
                case INTEGER -> longSumValues += Long.parseLong(value);
                case DOUBLE -> doubleSumValues += Double.parseDouble(value);
                default -> throw new InvalidExpression(NON_NUMERIC_FIELD_EXCEPTION);
            }
        }
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        if (numValues == 0) return new Pair<>("", dataType);

        return switch (dataType) {
            case INTEGER -> new Pair<>(String.valueOf(longSumValues / numValues), dataType);
            case DOUBLE -> new Pair<>(String.valueOf(doubleSumValues / numValues), dataType);
            default -> throw new InvalidExpression(NON_NUMERIC_FIELD_EXCEPTION);
        };
    }

    @Override
    public DataTypeEnum inferType() {
        return dataType;
    }
}
