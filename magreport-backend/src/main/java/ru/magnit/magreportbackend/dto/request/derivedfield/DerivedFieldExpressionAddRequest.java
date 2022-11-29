package ru.magnit.magreportbackend.dto.request.derivedfield;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.Expressions;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class DerivedFieldExpressionAddRequest {
    @JsonIgnore
    private Long ordinal;
    private Expressions type;
    private Long referenceId;
    private String constantValue;
    private DataTypeEnum constantType;
    private List<DerivedFieldExpressionAddRequest> parameters;
}
