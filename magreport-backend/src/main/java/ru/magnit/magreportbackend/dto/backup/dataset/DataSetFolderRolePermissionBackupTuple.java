package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;

public record DataSetFolderRolePermissionBackupTuple (

        Long dataSetFolderRolePermissionId,
        Long dataSetFolderRoleId,
        Long folderAuthorityId,
        LocalDateTime created,
        LocalDateTime modified
){}
