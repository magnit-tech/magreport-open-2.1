package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.derivedfield.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantValueExpressionTest {

    @Test
    void constantExpressionTest(){
        final var sourceExpression =
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("2");

        final var expression = sourceExpression.getType().init(sourceExpression, null);
        final var expressionResult = expression.calculate(0);

        assertEquals("2", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());

    }
}
