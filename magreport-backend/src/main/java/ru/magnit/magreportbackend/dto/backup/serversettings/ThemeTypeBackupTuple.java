package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ThemeTypeBackupTuple(
        Long themeTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
