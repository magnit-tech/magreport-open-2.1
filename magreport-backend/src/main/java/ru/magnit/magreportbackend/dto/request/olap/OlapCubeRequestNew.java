package ru.magnit.magreportbackend.dto.request.olap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.olap.PlacementType;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class OlapCubeRequestNew {
    private Long jobId;

    private LinkedHashSet<FieldDefinition> columnFields = new LinkedHashSet<>();

    private LinkedHashSet<FieldDefinition> rowFields = new LinkedHashSet<>();

    private List<MetricDefinitionNew> metrics = Collections.emptyList();

    private PlacementType metricPlacement;

    private FilterGroupNew filterGroup;

    private MetricFilterGroup metricFilterGroup;

    private Interval columnsInterval;

    private Interval rowsInterval;

    private List<SortingParams> columnSort = Collections.emptyList();

    private List<SortingParams> rowSort = Collections.emptyList();

    public Set<FieldDefinition> getAllFields(){
        final var result = new HashSet<FieldDefinition>();

        result.addAll(columnFields);
        result.addAll(rowFields);
        result.addAll(metrics.stream().map(MetricDefinitionNew::getField).toList());
        result.addAll(filterGroup.getAllFields());
        return result;
    }

    public boolean hasDerivedFields() {
        return getAllFields().stream()
            .anyMatch(field -> field.getFieldType() == OlapFieldTypes.DERIVED_FIELD);
    }
}
