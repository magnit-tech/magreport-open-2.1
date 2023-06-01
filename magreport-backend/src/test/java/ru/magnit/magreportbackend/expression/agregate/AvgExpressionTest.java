package ru.magnit.magreportbackend.expression.agregate;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.expression.ExpressionCreationContext;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AvgExpressionTest {
    @Test
    void AvgTest() {
        final var sourceExpression = new FieldExpressionResponse()
                .setType(Expressions.AVG)
                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                .setReferenceId(0L);

        final var fieldIndex = Map.of(new FieldDefinition(0L, OlapFieldTypes.REPORT_FIELD), new Pair<>(0, DataTypeEnum.INTEGER));
        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(fieldIndex, new String[][]{{"1","3"}}, null, null));
        expression.addValue(0, 0,0);
        expression.addValue(1, 0,0);
        final var expressionResult = expression.calculate(0);

        assertEquals("2", expressionResult.getL());
        assertEquals(DataTypeEnum.INTEGER, expressionResult.getR());
    }
}
