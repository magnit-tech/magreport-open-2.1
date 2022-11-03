package ru.magnit.magreportbackend.dto.backup.datasource;

import java.time.LocalDateTime;

public record DataSourceFolderRolePermissionBackupTuple(
        Long dataSourceFolderRolePermissionId,
        Long dataSourceFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified
){}
