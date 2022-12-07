package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.ReportFolder;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportFolderReportMapper implements Mapper<ReportFolder, ReportFolderBackupTuple> {
    @Override
    public ReportFolder from(ReportFolderBackupTuple source) {
        return new ReportFolder()
                .setId(source.reportFolderId())
                .setParentFolder(new ReportFolder(source.parentId()))
                .setName(source.name())
                .setDescription(source.description());
    }
}
