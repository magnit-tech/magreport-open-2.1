package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ServerMailTemplateBackupTuple(
        Long serverMailTemplateId,
        Long serverMailTemplateTypeId,
        Long userId,
        String code,
        String subject,
        String body,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
