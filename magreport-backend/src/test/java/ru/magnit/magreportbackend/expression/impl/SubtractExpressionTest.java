package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.derivedfield.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtractExpressionTest {

    @Test
    void subtractTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.SUBTRACT)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("15"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("3"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.DOUBLE)
                    .setConstantValue("4.5"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("-1")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression);
        final var expressionResult = expression.calculate(0);

        assertEquals("8.5", expressionResult.getL());
        assertEquals(DataTypeEnum.DOUBLE, expressionResult.getR());
    }

    @Test
    void subtractIntegerTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.SUBTRACT)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("15"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("3"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("-1")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression);
        final var expressionResult = expression.calculate(0);

        assertEquals("13", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
