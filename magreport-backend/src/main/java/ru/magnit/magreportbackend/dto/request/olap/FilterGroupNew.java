package ru.magnit.magreportbackend.dto.request.olap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;
import ru.magnit.magreportbackend.util.Pair;

import java.util.*;
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

    public FilterGroup asFilterGroup(Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes){
        return new FilterGroup(
                getOperationType(),
                isInvertResult(),
                getChildGroups().stream().map(o -> o.asFilterGroup(fieldIndexes)).toList(),
                getFilters().stream().map(filter -> new FilterDefinition(
                        filter.getField().getFieldType() == OlapFieldTypes.REPORT_FIELD ? filter.getField().getFieldId() : fieldIndexes.get(filter.getField()).getL(),
                        filter.getFilterType(),
                        filter.isInvertResult(),
                        filter.getRounding(),
                        filter.isCanRounding(),
                        filter.getValues()
                )).toList()
        );
    }
}
