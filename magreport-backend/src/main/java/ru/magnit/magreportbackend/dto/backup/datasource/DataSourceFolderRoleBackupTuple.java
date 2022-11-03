package ru.magnit.magreportbackend.dto.backup.datasource;

import java.time.LocalDateTime;

public record DataSourceFolderRoleBackupTuple(

        Long dataSourceFolderRoleId,
        Long dataSourceFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified

){}
