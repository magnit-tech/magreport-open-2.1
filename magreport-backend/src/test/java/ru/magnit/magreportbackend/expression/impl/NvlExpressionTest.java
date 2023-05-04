package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NvlExpressionTest {

    @Test
    void nvlExpressionTest(){
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.NVL)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue(null),
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("3")
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("3", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());

    }

    @Test
    void nvlExpressionDateTest(){
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.NVL)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.TIMESTAMP)
                                .setConstantValue(null),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.DATE)
                                .setConstantValue("2023-04-29")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("2023-04-29 00:00", expressionResult.getL());
        assertEquals(DataTypeEnum.TIMESTAMP, expressionResult.getR());
    }

    @Test
    void nvlExpressionDateTimeTest(){
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.NVL)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.DATE)
                                .setConstantValue("2023-04-29"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.TIMESTAMP)
                                .setConstantValue("2023-04-29 00:00:00")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        final var expressionResult = expression.calculate(0);

        assertEquals("2023-04-29 00:00", expressionResult.getL());
        assertEquals(DataTypeEnum.TIMESTAMP, expressionResult.getR());
    }
}
