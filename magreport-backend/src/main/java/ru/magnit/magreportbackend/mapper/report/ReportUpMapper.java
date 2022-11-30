package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.report.ReportFolder;
import ru.magnit.magreportbackend.dto.backup.report.ReportBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportUpMapper implements Mapper<Report, ReportBackupTuple> {
    @Override
    public Report from(ReportBackupTuple source) {
        return new Report()
                .setId(source.reportId())
                .setFolder(new ReportFolder(source.reportFolderId()))
                .setName(source.name())
                .setDescription(source.description())
                .setRequirementsLink(source.requirementsURL())
                .setDataSet(new DataSet(source.datasetId()));
    }
}
