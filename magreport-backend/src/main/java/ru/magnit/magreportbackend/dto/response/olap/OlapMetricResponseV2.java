package ru.magnit.magreportbackend.dto.response.olap;

import lombok.*;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OlapMetricResponseV2 {
    private List<List<String>> values;
    private DataTypeEnum dataType;
}
