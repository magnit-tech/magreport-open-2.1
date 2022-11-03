package ru.magnit.magreportbackend.mapper.folderreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.folderreport.FolderReport;
import ru.magnit.magreportbackend.dto.backup.folder.FolderReportBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FolderReportBackupMapper implements Mapper<FolderReportBackupTuple, FolderReport> {
    @Override
    public FolderReportBackupTuple from(FolderReport source) {
        return new FolderReportBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getReport().getId(),
                source.getUser().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
