package ru.magnit.magreportbackend.dto.backup.filtertemplate;

import java.time.LocalDateTime;

public record FilterTemplateOperationBackupTuple(

        Long filterTemplateOperationId,
        Long filterTemplateId,
        Long filterOperationTypeId,
        LocalDateTime created,
        LocalDateTime modified

) {
}
