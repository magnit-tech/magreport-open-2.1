package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;

public record DataSetFolderBackupTuple(

    Long datasetFolderId,
    Long parentId,
    String name,
    String description,
    LocalDateTime created,
    LocalDateTime modified
){}
