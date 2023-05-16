package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MillsecIntervalExtensionTest {
    @Test
    void MillsecIntervalTest() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.MILLSEC_INTERVAL)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.TIMESTAMP)
                    .setConstantValue("2023-03-28 09:27:01.512"),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.TIMESTAMP)
                    .setConstantValue("2023-03-28 09:27:02.513")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("1001", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }

    @Test
    void MillsecIntervalNvlTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.MILLSEC_INTERVAL)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.NVL)
                                .setParameters(List.of(
                                        new FieldExpressionResponse()
                                                .setType(Expressions.CONSTANT_VALUE)
                                                .setConstantType(DataTypeEnum.TIMESTAMP)
                                                .setConstantValue(null),
                                        new FieldExpressionResponse()
                                                .setType(Expressions.TODAY)

                                )),
                        new FieldExpressionResponse()
                                .setType(Expressions.NVL)
                                .setParameters(List.of(
                                        new FieldExpressionResponse()
                                                .setType(Expressions.CONSTANT_VALUE)
                                                .setConstantType(DataTypeEnum.TIMESTAMP)
                                                .setConstantValue(null),
                                        new FieldExpressionResponse()
                                                .setType(Expressions.TODAY)

                                ))
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("0", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
