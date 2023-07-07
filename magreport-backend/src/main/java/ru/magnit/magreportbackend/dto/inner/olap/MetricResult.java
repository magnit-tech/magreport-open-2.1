package ru.magnit.magreportbackend.dto.inner.olap;

import lombok.Getter;
import lombok.Setter;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;

@Getter
@Setter
public class MetricResult {
    String value;
    DataTypeEnum type;
    int column;
    int row;

    public MetricResult(String value, int column, int row) {
        this.value = value;
        this.column = column;
        this.row = row;
    }

    public MetricResult(String value, DataTypeEnum type, int column, int row) {
        this.value = value;
        this.type = type;
        this.column = column;
        this.row = row;
    }
}
