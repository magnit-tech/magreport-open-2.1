package ru.magnit.magreportbackend.dto.response.olap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OlapMetricResponseV2 {
    private String[][] values;
    private DataTypeEnum dataType;
}
