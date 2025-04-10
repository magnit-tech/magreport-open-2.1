package ru.magnit.magreportbackend.dto.request.derivedfield;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class DerivedFieldAddRequest {
    private Long id;
    private Long reportId;
    private Boolean isPublic = false;
    private String name;
    private String description;
    private DerivedFieldExpressionAddRequest expression;
    private String expressionText;

    public String getUniqueName(Long userId){
        if (Boolean.TRUE.equals(isPublic)) {
            return reportId + "_" + name;
        } else {
            return reportId + "_" + userId + "_" + name;
        }
    }
}
