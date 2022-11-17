package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.derivedfield.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiplyExpressionTest {

    @Test
    void multiplicationTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.MULTIPLY)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("2"),
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

        final var expression = sourceExpression.getType().init(sourceExpression, null);
        final var expressionResult = expression.calculate(0);

        assertEquals("-27.0", expressionResult.getL());
        assertEquals(DataTypeEnum.DOUBLE, expressionResult.getR());
    }

    @Test
    void multiplyIntegerTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.MULTIPLY)
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

        final var expression = sourceExpression.getType().init(sourceExpression, null);
        final var expressionResult = expression.calculate(0);

        assertEquals("-45", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
