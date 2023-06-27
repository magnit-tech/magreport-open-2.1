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

class JsonExpressionTest {

    @Test
    void jsonExpressionTest1() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(getJson()),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("authors[0].name")

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);


        assertEquals("ivanov", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());

    }


    @Test
    void jsonExpressionTest2() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(getJson()),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("authors[0].username")

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);


        assertNull(expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());

    }


    @Test
    void jsonExpressionTest3() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("!!!"),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("authors[0].username")

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));

        assertThrows(InvalidExpression.class, () -> expression.calculate(0));
    }

    @Test
    void jsonExpressionTest4() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(getJson()),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("authors[0]")

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));
        final var expressionResult = expression.calculate(0);


        assertEquals("{\"name\":\"ivanov\"}", expressionResult.getL());
        assertEquals(DataTypeEnum.STRING, expressionResult.getR());

    }

    @Test
    void jsonExpressionTest5() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(getJson()),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(null)

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));

        assertThrows(InvalidExpression.class, () -> expression.calculate(0));
    }

    @Test
    void jsonExpressionTest6() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue(getJson()),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue("authors[0]")

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));

        assertThrows(InvalidExpression.class, () -> expression.calculate(0));
    }

    @Test
    void jsonExpressionTest7() {

        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.JSON_FIELD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.STRING)
                                .setConstantValue(getJson()),
                        new FieldExpressionResponse()
                                .setType(Expressions.CONSTANT_VALUE)
                                .setConstantType(DataTypeEnum.INTEGER)
                                .setConstantValue("authors[0]")

                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, new DerivedFieldResponse().setId(1L).setName("Test field"), null));

        assertThrows(InvalidExpression.class, () -> expression.calculate(0));
    }





    private String getJson() {
       return "{" +
               "   \"authors\" : [{\"name\" : \"ivanov\"}, {\"name\": \"petrov\"}]," +
               "   \"pages\": 315\n" +
               "}";
    }
}
