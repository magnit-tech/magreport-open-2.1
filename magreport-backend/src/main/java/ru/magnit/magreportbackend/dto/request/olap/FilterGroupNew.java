package ru.magnit.magreportbackend.dto.request.olap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FilterGroupNew {
    private BinaryBooleanOperations operationType;
    private boolean invertResult;
    private List<FilterGroupNew> childGroups = Collections.emptyList();
    private List<FilterDefinitionNew> filters = Collections.emptyList();

    public Set<FieldDefinition> getAllFields(){
        var result = filters.stream().map(FilterDefinitionNew::getField).collect(Collectors.toCollection(HashSet::new));
        result.addAll(childGroups.stream().flatMap(g -> g.getAllFields().stream()).collect(Collectors.toSet()));
        return result;
    }
}
