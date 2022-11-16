package ru.magnit.magreportbackend.dto.request.olap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.olap.AggregationType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MetricDefinitionNew {
    private FieldDefinition field;
    private AggregationType aggregationType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricDefinitionNew that = (MetricDefinitionNew) o;

        if (!getField().equals(that.getField())) return false;
        return getAggregationType() == that.getAggregationType();
    }

    @Override
    public int hashCode() {
        int result = getField().hashCode();
        result = 31 * result + getAggregationType().hashCode();
        return result;
    }
}
