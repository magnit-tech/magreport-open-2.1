package ru.magnit.magreportbackend.dto.request.olap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class OlapExportPivotTableRequest {

    private OlapCubeRequestNew  cubeRequest;
    private Long configuration;
    private boolean stylePivotTable;

}
