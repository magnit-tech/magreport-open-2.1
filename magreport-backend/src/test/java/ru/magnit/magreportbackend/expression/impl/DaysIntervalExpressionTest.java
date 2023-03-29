package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DaysIntervalExpressionTest {
    @Test
    void DaysIntervalTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.DAYS_INTERVAL)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.DATE)
                    .setConstantValue("2023-03-28"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.DATE)
                    .setConstantValue("2023-03-29")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("1", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
