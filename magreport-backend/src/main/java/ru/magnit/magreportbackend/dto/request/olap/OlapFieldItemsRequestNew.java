package ru.magnit.magreportbackend.dto.request.olap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class OlapFieldItemsRequestNew {

    private Long jobId;
    private Long fieldId;
    private Long from;
    private Long count;
    private FilterGroupNew filterGroup;
    private MetricFilterGroup metricFilterGroup;
    private List<MetricDefinitionNew> metrics = Collections.emptyList();
    private LinkedHashSet<FieldDefinition> columnFields = new LinkedHashSet<>();
    private LinkedHashSet<FieldDefinition> rowFields = new LinkedHashSet<>();

    public Set<FieldDefinition> getAllFields() {
        final var result = new HashSet<FieldDefinition>();

        result.addAll(columnFields);
        result.addAll(rowFields);
        result.addAll(metrics.stream().map(MetricDefinitionNew::getField).toList());
        if (filterGroup != null) result.addAll(filterGroup.getAllFields());
        return result;
    }

    public boolean hasDerivedFields() {
        return getAllFields().stream()
                .anyMatch(field -> field.getFieldType() == OlapFieldTypes.DERIVED_FIELD);
    }
}

