package ru.magnit.magreportbackend.dto.response.olap;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OlapCubeResponseV2 {
    private List<List<String>> columnValues;
    private List<List<String>> rowValues;
    private List<OlapMetricResponseV2> metricValues;
    private int totalColumns;
    private int totalRows;
}
