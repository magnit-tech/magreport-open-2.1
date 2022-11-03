package ru.magnit.magreportbackend.dto.backup.datasource;

import java.time.LocalDateTime;

public record DataSourceBackupTuple(
        Long dataSourceId,
        Long dataSourceTypeId,
        Long dataSourceFolderId,
        Long userId,
        String jdbcUrl,
        String userName,
        String password,
        Short poolSize,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
