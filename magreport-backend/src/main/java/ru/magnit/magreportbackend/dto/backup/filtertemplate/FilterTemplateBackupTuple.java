package ru.magnit.magreportbackend.dto.backup.filtertemplate;

import java.time.LocalDateTime;

public record FilterTemplateBackupTuple(

        Long filterTemplateId,
        Long filterTypeId,
        Long filterTemplateFolder,
        Long userId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
