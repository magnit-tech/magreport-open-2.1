package ru.magnit.magreportbackend.dto.request.olap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class OlapFieldItemsRequest {

    private Long jobId;
    private Long fieldId;
    private Long from;
    private Long count;
    private FilterGroup filterGroup;
    private MetricFilterGroup metricFilterGroup;
    private List<MetricDefinition> metrics = Collections.emptyList();
    private LinkedHashSet<Long> columnFields = new LinkedHashSet<>();
    private LinkedHashSet<Long> rowFields = new LinkedHashSet<>();
    public int getEndPoint(){
        return (int) (from + count);
    }
}

