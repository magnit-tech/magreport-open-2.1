package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthSourceFieldFIFieldBackupTuple(

        Long externalAuthSourceFieldFIFieldId,
        Long externalAuthSourceFieldId,
        Long filterInstanceFieldId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
