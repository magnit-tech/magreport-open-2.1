package ru.magnit.magreportbackend.dto.request.olap;

import lombok.*;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.olap.PlacementType;
import ru.magnit.magreportbackend.dto.response.derivedfield.FieldExpressionResponse;

import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@SuppressWarnings("Duplicates")
public class OlapCubeRequestV2 {
    private Long jobId;
    private LinkedHashSet<FieldDefinition> columnFields = new LinkedHashSet<>();
    private LinkedHashSet<FieldDefinition> rowFields = new LinkedHashSet<>();
    private List<FieldExpressionResponse> metrics = Collections.emptyList();
    private PlacementType metricPlacement;
    private FilterGroupNew filterGroup;
    private MetricFilterGroup metricFilterGroup;
    private Interval columnsInterval;
    private Interval rowsInterval;
    private List<SortingParams> columnSort = Collections.emptyList();
    private List<SortingParams> rowSort = Collections.emptyList();

    public Set<FieldDefinition> getAllFields() {
        final var result = new HashSet<FieldDefinition>();

        result.addAll(columnFields);
        result.addAll(rowFields);
        result.addAll(metrics.stream().flatMap(m -> m.getAllFieldLinks().stream()).toList());
        if (filterGroup != null) result.addAll(filterGroup.getAllFields());
        return result;
    }

    public boolean hasDerivedFields() {
        return getAllFields().stream()
                .anyMatch(field -> field.getFieldType() == OlapFieldTypes.DERIVED_FIELD);
    }
}
