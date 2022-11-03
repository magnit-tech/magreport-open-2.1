package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.ReportFolderRole;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportFolderRoleBackupMapper implements Mapper<ReportFolderRoleBackupTuple, ReportFolderRole> {
    @Override
    public ReportFolderRoleBackupTuple from(ReportFolderRole source) {
        return new ReportFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
