package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTupleField;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobTupleFieldData;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobTupleFieldDataMapper implements Mapper<ReportJobTupleFieldData, ReportJobTupleField> {

    @Override
    public ReportJobTupleFieldData from(ReportJobTupleField source) {
        final var datasetField = source.getFilterReportField().getFilterInstanceField().getDataSetField();
        final var datasetId = datasetField == null ? null :datasetField.getDataSet().getId();
        final var datasetFieldId = datasetField == null ? null : datasetField.getId();
        return new ReportJobTupleFieldData(
                source.getFilterReportField().getId(),
                datasetId,
                datasetFieldId,
                source.getFilterReportField().getFilterInstanceField().getLevel(),
                null,
                null,
                source.getValue()
        );
    }
}
