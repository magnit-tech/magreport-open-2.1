package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.derivedfield.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrLenExpressionTest {

    @Test
    void strlenTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.STRLEN)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.STRING)
                    .setConstantValue("Test string")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, null);
        final var expressionResult = expression.calculate(0);

        assertEquals("11", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
