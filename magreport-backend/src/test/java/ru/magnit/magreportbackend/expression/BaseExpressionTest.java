package ru.magnit.magreportbackend.expression;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;
import ru.magnit.magreportbackend.dto.response.derivedfield.DerivedFieldResponse;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;
import ru.magnit.magreportbackend.util.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
class BaseExpressionTest {

    @Test
    void testErrorDescription() {
        final var sourceExpression = new FieldExpressionResponse()
            .setType(Expressions.ADD)
            .setParameters(List.of(
                new FieldExpressionResponse()
                    .setType(Expressions.CONSTANT_VALUE)
                    .setConstantType(DataTypeEnum.INTEGER)
                    .setConstantValue("1"),
                new FieldExpressionResponse()
                    .setType(Expressions.ADD)
                    .setParameters(List.of(
                        new FieldExpressionResponse()
                            .setType(Expressions.CONSTANT_VALUE)
                            .setConstantType(DataTypeEnum.DOUBLE)
                            .setConstantValue("2"),
                        new FieldExpressionResponse()
                            .setType(Expressions.CONSTANT_VALUE)
                            .setConstantType(DataTypeEnum.INTEGER)
                            .setConstantValue(null)
                    )),
                new FieldExpressionResponse()
                    .setType(Expressions.ADD)
                    .setParameters(List.of(
                        new FieldExpressionResponse()
                            .setType(Expressions.CONSTANT_VALUE)
                            .setConstantType(DataTypeEnum.DOUBLE)
                            .setConstantValue("4"),
                        new FieldExpressionResponse()
                            .setType(Expressions.CONSTANT_VALUE)
                            .setConstantType(DataTypeEnum.INTEGER)
                            .setConstantValue("5")
                    ))
            ));

        final var expression = sourceExpression.getType().init(sourceExpression, new ExpressionCreationContext(
            Map.of(
                new FieldDefinition(1L, OlapFieldTypes.DERIVED_FIELD),
                new Pair<>(0, DataTypeEnum.INTEGER)
            ),
            new String[][]{{"123"}},
            new DerivedFieldResponse(1L, 1L, DataTypeEnum.INTEGER, "Derived field", "Description", 1L, "Test user", LocalDateTime.now(), LocalDateTime.now(), null, "")
        ));
        try {
            expression.calculate(0);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
        }
    }
}
