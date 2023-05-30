package ru.magnit.magreportbackend.expression.agregate;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CountExpressionTest {
    @Test
    void CountTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.COUNT)
                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                .setReferenceId(0L);

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        expression.addValue(null, 0,0);
        final var expressionResult = expression.calculate(0);

        assertEquals("1", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }

    @Test
    void AddValueTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.ADD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.COUNT)
                                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                                .setReferenceId(0L),
                        new FieldExpressionResponse()
                                .setType(Expressions.COUNT)
                                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                                .setReferenceId(0L)
                ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(null, null, null));
        expression.addValue(null, 0,0);
        final var expressionResult = expression.calculate(0);

        assertEquals("2", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }

    @Test
    void LinksTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.ADD)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.COUNT)
                                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                                .setReferenceId(0L),
                        new FieldExpressionResponse()
                                .setType(Expressions.COUNT)
                                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                                .setReferenceId(0L)
                ));

        final var links = sourceExpression.getAllFieldLinks();

        assertEquals(1, links.size());
        assertEquals(links.get(0), new FieldDefinition(0L, OlapFieldTypes.REPORT_FIELD));
    }
}
