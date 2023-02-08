package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

public class RowNumberExpression extends BaseExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>("", DataTypeEnum.INTEGER);

    public RowNumberExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        result.setL("" + (rowNumber + 1));
        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
