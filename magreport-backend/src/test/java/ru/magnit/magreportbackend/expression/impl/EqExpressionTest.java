package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EqExpressionTest {
    @Test
    void EqTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.EQ)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("2"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("2")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("true", expressionResult.getL());
        assertEquals(DataTypeEnum.BOOLEAN, expressionResult.getR());
    }

    @Test
    void EqIntDoubleTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.EQ)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("2"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.DOUBLE)
                                .setConstantValue("2")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("true", expressionResult.getL());
        assertEquals(DataTypeEnum.BOOLEAN, expressionResult.getR());
    }
}
