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
public class OlapExportPivotTableRequestV2 {

        private OlapCubeRequestV2  cubeRequest;
        private Long configuration;
        private boolean stylePivotTable;
}
