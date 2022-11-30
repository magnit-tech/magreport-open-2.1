package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.report.PivotFieldType;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.report.ReportField;
import ru.magnit.magreportbackend.dto.backup.report.ReportFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportFieldUpMapper implements Mapper<ReportField, ReportFieldBackupTuple> {
    @Override
    public ReportField from(ReportFieldBackupTuple source) {
        return new ReportField()
                .setId(source.reportFieldId())
                .setDataSetField(new DataSetField(source.datasetFieldId()))
                .setReport(new Report(source.reportId()))
                .setName(source.name())
                .setDescription(source.description())
                .setOrdinal(source.ordinal())
                .setVisible(source.visible())
                .setPivotFieldType(new PivotFieldType(source.pivotFieldTypeId()));
    }
}
