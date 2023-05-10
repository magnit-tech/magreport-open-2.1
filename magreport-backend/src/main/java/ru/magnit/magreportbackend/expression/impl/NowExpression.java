package ru.magnit.magreportbackend.expression.impl;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.BaseExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDateTime;

public class NowExpression  extends BaseExpression {
    private final Pair<String, DataTypeEnum> result = new Pair<>(LocalDateTime.now().toString().replace("T", " "), DataTypeEnum.TIMESTAMP);

    public NowExpression(FieldExpressionResponse fieldExpression, ExpressionCreationContext context) {
        super(fieldExpression, context);
    }

    @Override
    public Pair<String, DataTypeEnum> calculate(int rowNumber) {
        return result;
    }

    @Override
    public DataTypeEnum inferType() {
        return result.getR();
    }
}
