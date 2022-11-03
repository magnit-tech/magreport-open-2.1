package ru.magnit.magreportbackend.dto.backup.filtertemplate;

import java.time.LocalDateTime;

public record FilterTemplateFieldBackupTuple(

        Long filterTemplateFieldId,
        Long filterTemplateId,
        Long filterFieldTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
