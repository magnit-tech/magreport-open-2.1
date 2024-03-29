package ru.magnit.magreportbackend.expression.agregate;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.AggregateExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;
import ru.magnit.magreportbackend.util.Triple;

import java.util.Set;

public class CountDistinctExpression extends AggregateExpression {
    private long result;
    private final Set<Triple<Integer, Integer, String>> distinctSet;

    public CountDistinctExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
        distinctSet = context.distinctSets().get(fieldExpression);
    }

    @Override
    public void addValue(int cubeRow, int rowNumber, int columnNumber) {
        final var value = cubeValues[fieldIndex][cubeRow];
        if (distinctSet.add(new Triple<>(columnNumber, rowNumber, value))){
            result++;
        }
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        return new Pair<>(Long.toString(result), DataTypeEnum.INTEGER);
    }

    @Override
    public DataTypeEnum inferType() {
        return DataTypeEnum.INTEGER;
    }
}
