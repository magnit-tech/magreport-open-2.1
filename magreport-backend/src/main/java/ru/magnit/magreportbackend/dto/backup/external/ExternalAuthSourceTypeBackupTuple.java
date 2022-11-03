package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthSourceTypeBackupTuple(

        Long externalAuthSourceTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
