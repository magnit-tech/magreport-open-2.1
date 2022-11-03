package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ServerSettingsFolderBackupTuple(
        Long serverSettingsFolderId,
        Long ordinal,
        Long parentId,
        String code,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
