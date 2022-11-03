package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthSourceFieldBackupTuple(

        Long externalAuthSourceFieldId,
        Long externalAuthSourceFieldTypeId,
        Long externalAuthSourceId,
        Long datasetFieldId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
