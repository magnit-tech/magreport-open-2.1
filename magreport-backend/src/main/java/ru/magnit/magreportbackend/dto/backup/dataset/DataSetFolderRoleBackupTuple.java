package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;

public record DataSetFolderRoleBackupTuple(

        Long datasetFolderRoleId,
        Long datasetFolderId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified
){}
