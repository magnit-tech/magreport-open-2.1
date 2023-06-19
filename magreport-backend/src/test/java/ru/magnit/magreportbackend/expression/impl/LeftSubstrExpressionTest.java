package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeftSubstrExpressionTest {
    @Test
    void leftSubstrTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.LEFT_SUBSTR)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.STRING)
                    .setConstantValue("Test string"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("4")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("Test", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }

    @Test
    void OverflowLeftSubstrTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.LEFT_SUBSTR)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("Test string"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("15")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("Test string", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }
}
