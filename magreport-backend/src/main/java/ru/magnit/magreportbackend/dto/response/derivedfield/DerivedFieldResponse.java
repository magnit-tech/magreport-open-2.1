package ru.magnit.magreportbackend.dto.response.derivedfield;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;

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
    private Boolean isPublic;
    private DataTypeEnum dataType;
    private String name;
    private String description;
    private Long userId;
    private String userName;
    private LocalDateTime created;
    private LocalDateTime modified;
    private FieldExpressionResponse expression;
    private String expressionText;

    @JsonIgnore
    public List<FieldExpressionResponse> getAllExpressions(){
        final var result = new ArrayList<FieldExpressionResponse>();
        result.add(expression);
        result.addAll(expression.getChildExpressions());
        return result;
    }

    @JsonIgnore
    public List<Long> getUsedDerivedFieldIds() {
        return getAllExpressions().stream()
            .filter(expr -> expr.getType() == Expressions.DERIVED_FIELD_VALUE)
            .map(FieldExpressionResponse::getReferenceId)
            .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DerivedFieldResponse that = (DerivedFieldResponse) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
