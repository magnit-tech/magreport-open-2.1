package ru.magnit.magreportbackend.dto.backup.dataset;

import java.time.LocalDateTime;

public record DataTypeBackupTuple(
    Long id,
    String name,
    String description,
    LocalDateTime created,
    LocalDateTime modified
) {
}
