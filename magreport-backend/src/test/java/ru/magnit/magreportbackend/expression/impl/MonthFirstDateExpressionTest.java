package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonthFirstDateExpressionTest {
    @Test
    void MonthFirstDateTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.MONTH_FIRST_DATE)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.DATE)
                    .setConstantValue("2023-03-28")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("2023-03-01", expressionResult.getL());
        assertEquals(DataTypeEnum.DATE, expressionResult.getR());
    }
}
