package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.derivedfield.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubstrExpressionTest {

    @Test
    void substrTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.SUBSTR)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.STRING)
                    .setConstantValue("Test string"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("0"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("4")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression);
        final var expressionResult = expression.calculate(0);

        assertEquals("Test", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }
}
