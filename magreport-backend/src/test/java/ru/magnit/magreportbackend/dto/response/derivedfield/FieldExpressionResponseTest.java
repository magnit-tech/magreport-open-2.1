package ru.magnit.magreportbackend.dto.response.derivedfield;

import org.junit.jupiter.api.Test;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FieldExpressionResponseTest {

    @Test
    void getAllFieldLinks() {
        final var expression = new FieldExpressionResponse()
                .setType(Expressions.DIVIDE)
                .setParameters(List.of(
                        new FieldExpressionResponse()
                                .setType(Expressions.SUM)
                                .setFieldType(OlapFieldTypes.DERIVED_FIELD)
                                .setReferenceId(0L),
                        new FieldExpressionResponse()
                                .setType(Expressions.SUM)
                                .setFieldType(OlapFieldTypes.REPORT_FIELD)
                                .setReferenceId(1L)
                ));

        final var derivedFields = expression.getAllFieldLinks();
        assertEquals(2, derivedFields.size());
        assertTrue(derivedFields.stream().anyMatch(def -> def.getFieldId() == 0L && def.getFieldType() == OlapFieldTypes.DERIVED_FIELD));
        assertTrue(derivedFields.stream().anyMatch(def -> def.getFieldId() == 1L && def.getFieldType() == OlapFieldTypes.REPORT_FIELD));
    }
}
