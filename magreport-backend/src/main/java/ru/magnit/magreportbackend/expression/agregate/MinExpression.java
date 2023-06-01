package ru.magnit.magreportbackend.expression.agregate;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.AggregateExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;
import ru.magnit.magreportbackend.util.StringUtils;

public class MinExpression extends AggregateExpression {
    private final DataTypeEnum dataType;
    private Object maxValue;

    public MinExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        final var fieldDefinition = new FieldDefinition(fieldExpression.getReferenceId(), fieldExpression.getFieldType());
        dataType = context.fieldIndexes().get(fieldDefinition).getR();
    }

    @Override
    public void addValue(int cubeRow, int rowNumber, int columnNumber) {
        final var value = cubeValues[fieldIndex][cubeRow];
        if (value != null) {
            switch (dataType) {
                case INTEGER ->
                        maxValue = maxValue == null ? Long.valueOf(value) : Long.valueOf(Long.min((Long) maxValue, Long.parseLong(value)));
                case DOUBLE ->
                        maxValue = maxValue == null ? Double.valueOf(value) : Double.valueOf(Double.min((Double) maxValue, Double.parseDouble(value)));
                case STRING, DATE, TIMESTAMP, BOOLEAN ->
                        maxValue = maxValue == null ? value : StringUtils.min((String) maxValue, value);
            }
        }
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        return new Pair<>(maxValue.toString(), dataType);
    }

    @Override
    public DataTypeEnum inferType() {
        return dataType;
    }
}
