package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record JobTokenBackupTuple(
        String jobToken,
        Long reportJobId,
        Long excelTemplateId,
        String email,
        LocalDateTime created
) {
}
