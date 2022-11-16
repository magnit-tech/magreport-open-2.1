package ru.magnit.magreportbackend.dto.inner.olap;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.dto.request.olap.FieldDefinition;
import ru.magnit.magreportbackend.util.Pair;

import java.util.Map;
import java.util.Objects;

public record CubeDataNew(
    ReportData reportMetaData,
    int numRows,
    Map<FieldDefinition, Pair<Integer, DataTypeEnum>> fieldIndexes,
    String[][] data
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CubeDataNew cubeData)) return false;
        return reportMetaData.equals(cubeData.reportMetaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportMetaData);
    }

    @Override
    public String toString() {
        return "CubeData{" +
            "reportMetaData=" + reportMetaData +
            '}';
    }
}
