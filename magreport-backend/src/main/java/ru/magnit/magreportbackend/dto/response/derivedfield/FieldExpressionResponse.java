package ru.magnit.magreportbackend.dto.response.derivedfield;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FieldExpressionResponse {
    private Expressions type;
    private Long referenceId;
    private String constantValue;
    private DataTypeEnum constantType;
    private List<FieldExpressionResponse> parameters = new ArrayList<>();

    public List<FieldExpressionResponse> getChildExpressions() {
        final var result = new ArrayList<>(parameters);
        for (final var parameter: parameters) {
            result.addAll(parameter.getChildExpressions());
        }

        return result;
    }

    public List<FieldDefinition> getAllFieldLinks() {
        final var result = new ArrayList<FieldDefinition>();

        if (type == Expressions.REPORT_FIELD_VALUE ){
            result.add(new FieldDefinition(referenceId, OlapFieldTypes.REPORT_FIELD));
        } else if (type == Expressions.DERIVED_FIELD_VALUE ){
            result.add(new FieldDefinition(referenceId, OlapFieldTypes.DERIVED_FIELD));
        }

        for (final var parameter : parameters) {
            result.addAll(parameter.getAllFieldLinks());
        }

        return result;
    }
}
