package ru.magnit.magreportbackend.dto.inner.olap;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.magnit.magreportbackend.dto.inner.TaskInfo;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldMetadataResponse;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExportPivotConfiguration {

    private OlapCubeRequest cubeRequest;
    private OlapCubeResponse data;
    private Long code;
    private boolean stylePivotTable;
    private boolean encrypt;
    private JsonNode config;
    private List<ReportFieldMetadataResponse> metadataFields = Collections.emptyList();
    private TaskInfo taskInfo;


}
