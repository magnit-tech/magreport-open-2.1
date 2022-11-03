package ru.magnit.magreportbackend.mapper.folderreport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.folderreport.FolderRolePermission;
import ru.magnit.magreportbackend.dto.backup.folder.FolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FolderRolePermissionBackupMapper implements Mapper<FolderRolePermissionBackupTuple, FolderRolePermission> {
    @Override
    public FolderRolePermissionBackupTuple from(FolderRolePermission source) {
        return new FolderRolePermissionBackupTuple(
                source.getId(),
                source.getFolderRole().getId(),
                source.getAuthority().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
