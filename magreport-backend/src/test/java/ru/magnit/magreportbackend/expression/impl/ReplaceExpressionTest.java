package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReplaceExpressionTest {

    @Test
    void replaceTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.REPLACE)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.STRING)
                    .setConstantValue("Test string"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.STRING)
                    .setConstantValue("string"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.STRING)
                    .setConstantValue("word")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);

        assertEquals("Test word", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }
}
