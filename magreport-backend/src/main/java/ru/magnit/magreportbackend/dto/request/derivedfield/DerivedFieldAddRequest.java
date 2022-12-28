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
    private String name;
    private String description;
    private DerivedFieldExpressionAddRequest expression;
    private String expressionText;
}
