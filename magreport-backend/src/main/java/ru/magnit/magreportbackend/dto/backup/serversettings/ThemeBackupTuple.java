package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ThemeBackupTuple(
        Long themeId,
        Long themeTypeId,
        Long userId,
        boolean favorite,
        String name,
        String description,
        LocalDateTime creared,
        LocalDateTime modified,
        String data
) {
}
