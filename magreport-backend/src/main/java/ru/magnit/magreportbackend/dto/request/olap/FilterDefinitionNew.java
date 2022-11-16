package ru.magnit.magreportbackend.dto.request.olap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.olap.FilterType;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FilterDefinitionNew {
    private FieldDefinition field;
    private FilterType filterType;
    private boolean invertResult;
    private int rounding;
    private boolean canRounding;
    private List<String> values = Collections.emptyList();
}
