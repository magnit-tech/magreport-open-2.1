package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RowNumberExpressionTest {
    @Test
    void rowNumberTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.ROW_NUMBER);

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("1", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
