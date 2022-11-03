package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ServerSettingsBackupTuple(
        Long serverSettingsId,
        Integer ordinal,
        Long folderId,
        boolean encoded,
        String code,
        String name,
        String description,
        String val,
        LocalDateTime created,
        LocalDateTime modified
) {
}
