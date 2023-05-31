package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrentDateExpressionTest {
    @Test
    void currentDateTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.CURRENT_DATE);

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals(LocalDate.now().toString(), expressionResult.getL());
        assertEquals(DataTypeEnum.DATE, expressionResult.getR());
    }
}
