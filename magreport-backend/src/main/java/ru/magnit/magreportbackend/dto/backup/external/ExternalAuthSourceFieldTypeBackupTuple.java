package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthSourceFieldTypeBackupTuple (

        Long externalAuthSourceFieldTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

){}
