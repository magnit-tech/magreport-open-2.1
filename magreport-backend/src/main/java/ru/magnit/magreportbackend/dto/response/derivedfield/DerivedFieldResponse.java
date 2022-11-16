package ru.magnit.magreportbackend.dto.response.derivedfield;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.derivedfield.Expressions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class DerivedFieldResponse {
    private Long id;
    private Long reportId;
    private String name;
    private String description;
    private Long userId;
    private String userName;
    private LocalDateTime created;
    private LocalDateTime modified;
    private FieldExpressionResponse expression;

    public List<FieldExpressionResponse> getAllExpressions(){
        final var result = new ArrayList<FieldExpressionResponse>();
        result.add(expression);
        result.addAll(expression.getChildExpressions());
        return result;
    }

    public List<Long> getUsedDerivedFieldIds() {
        return getAllExpressions().stream()
            .filter(expr -> expr.getType() == Expressions.DERIVED_FIELD_VALUE)
            .map(FieldExpressionResponse::getReferenceId)
            .toList();
    }
}
