package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToDatetimeExpressionTest {
    @Test
    void toDateTimeTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.TO_DATETIME)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.DATE)
                                .setConstantValue("2023-04-29")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("2023-04-29 00:00:00", expressionResult.getL());
        assertEquals(DataTypeEnum.TIMESTAMP, expressionResult.getR());
    }

    @Test
    void toDateTimeWithTimeTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.TO_DATETIME)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.TIMESTAMP)
                                .setConstantValue("2023-04-29 01:00:00"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("07:00:00")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("2023-04-29 07:00:00", expressionResult.getL());
        assertEquals(DataTypeEnum.TIMESTAMP, expressionResult.getR());
    }
}
