package ru.magnit.magreportbackend.expression.impl;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.exception.InvalidExpression;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitStrExpressionTest {

    @Test
    void splitStrTest1(){

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.SPLIT)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("First test string"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(" "),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("1")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);


        assertEquals("test", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }

    @Test
    void splitStrTest2(){

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.SPLIT)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(null),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(" "),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("1")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);


        assertNull( expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }

    @Test
    void splitStrTest3(){

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.SPLIT)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("Third test string"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(" "),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("4")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);


        assertNull( expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());
    }

    @Test
    void splitStrTest4(){

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.SPLIT)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("Third test string"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(null),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("2")
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));

        assertThrows(InvalidExpression.class, () -> expression.calculate(0));
    }

    @Test
    void splitStrTest5(){

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.SPLIT)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("Fourth test string"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(" "),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue(null)
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));

        assertThrows(InvalidExpression.class, () -> expression.calculate(0));
    }
}
