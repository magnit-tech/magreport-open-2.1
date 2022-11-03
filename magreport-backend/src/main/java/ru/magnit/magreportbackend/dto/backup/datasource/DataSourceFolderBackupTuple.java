package ru.magnit.magreportbackend.dto.backup.datasource;

import java.time.LocalDateTime;

public record DataSourceFolderBackupTuple(
    Long dataSourceFolderId,
    Long parentId,
    String name,
    String description,
    LocalDateTime created,
    LocalDateTime modified
    ){}
