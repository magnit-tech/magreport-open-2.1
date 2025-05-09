package ru.magnit.magreportbackend.dto.request.filterreport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportOperationTypeEnum;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class FilterAddRequest {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Long ordinal;
    private Boolean hidden;
    private Boolean mandatory;
    private Boolean rootSelectable;
    private Long filterInstanceId;
    private Long maxCountItems;
    private List<FilterFieldAddRequest> fields = Collections.emptyList();
    private List<FilterReportOperationTypeEnum> filterReportModes = Collections.emptyList();
}
