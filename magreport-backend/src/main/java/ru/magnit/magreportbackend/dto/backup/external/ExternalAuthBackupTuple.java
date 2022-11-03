package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthBackupTuple(

        Long externalAuthId,
        Long roleTypeId,
        Long userId,
        String name,
        String description,
        Boolean isDefaultDomain,
        LocalDateTime created,
        LocalDateTime modified
){}