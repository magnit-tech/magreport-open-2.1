package ru.magnit.magreportbackend.expression.agregate;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.AggregateExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

public class CountExpression extends AggregateExpression {

    private long result;

    public CountExpression(FieldExpressionResponse ignored, ExpressionCreationContext context) {
        super(ignored, context);
    }

    @Override
    public void addValue(String value, int rowNumber, int columnNumber) {
        result++;
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
