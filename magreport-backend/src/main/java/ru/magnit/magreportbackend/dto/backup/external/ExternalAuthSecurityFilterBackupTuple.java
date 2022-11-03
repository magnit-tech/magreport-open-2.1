package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthSecurityFilterBackupTuple(

        Long externalAuthSecurityFilterId,
        Long externalAuthSourceId,
        Long securityFilterId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
