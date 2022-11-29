package ru.magnit.magreportbackend.dto.response.derivedfield;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;

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
    private List<FieldExpressionResponse> parameters;

    public List<FieldExpressionResponse> getChildExpressions() {
        final var result = new ArrayList<>(parameters);
        for (final var parameter: parameters) {
            result.addAll(parameter.getChildExpressions());
        }

        return result;
    }
}
