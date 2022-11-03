package ru.magnit.magreportbackend.dto.backup.datasource;

import java.time.LocalDateTime;

public record DataSourceTypeBackupTuple(
        Long dataSourceTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
