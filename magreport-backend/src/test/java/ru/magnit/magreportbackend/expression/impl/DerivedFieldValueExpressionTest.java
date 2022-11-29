package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DerivedFieldValueExpressionTest {

    @Test
    void derivedFieldValueExpressionTest() {
        final var sourceExpression =
            new FieldExpressionResponse()
                .setType(Expressions.DERIVED_FIELD_VALUE)
                .setReferenceId(1L);

        final var context = new ExpressionCreationContext(
            Map.of(
                new FieldDefinition(1L, OlapFieldTypes.DERIVED_FIELD),
                new Pair<>(0, DataTypeEnum.INTEGER)
            ),
            new String[][]{{"123"}},
        new DerivedFieldResponse(1L,1L, "Derived field", "Description", 1L, "Test user", LocalDateTime.now(), LocalDateTime.now(), null)
   );

        final var expression = sourceExpression.getType().init(sourceExpression, context);
        final var expressionResult = expression.calculate(0);

        assertEquals("123", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
