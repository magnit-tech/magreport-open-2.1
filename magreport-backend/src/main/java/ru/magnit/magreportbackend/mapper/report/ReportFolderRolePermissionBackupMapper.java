package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.report.ReportFolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportFolderRolePermissionBackupMapper implements Mapper<ReportFolderRolePermissionBackupTuple, ReportFolderRolePermission> {
    @Override
    public ReportFolderRolePermissionBackupTuple from(ReportFolderRolePermission source) {
        return new ReportFolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
