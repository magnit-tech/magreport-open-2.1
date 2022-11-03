package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.ReportFolder;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportFolderBackupMapper implements Mapper<ReportFolderBackupTuple, ReportFolder> {
    @Override
    public ReportFolderBackupTuple from(ReportFolder source) {
        return new ReportFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
