package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RightSubstrExpressionTest {
    @Test
    void rightSubstrTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.RIGHT_SUBSTR)
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

        assertEquals("ring", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }

    @Test
    void OverflowRightSubstrTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.RIGHT_SUBSTR)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("Test string"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("16")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("Test string", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }
}
