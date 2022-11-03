package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ServerMailTemplateTypeBackupTuple(

        Long serverMailTemplateTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
