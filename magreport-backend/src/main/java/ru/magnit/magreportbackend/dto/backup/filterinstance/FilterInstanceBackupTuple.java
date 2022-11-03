package ru.magnit.magreportbackend.dto.backup.filterinstance;

import java.time.LocalDateTime;

public record FilterInstanceBackupTuple(

        Long filterInstanceId,
        Long filterInstanceFolderId,
        Long filterTemplateId,
        Long datasetId,
        Long userId,
        String name,
        String description,
        String code,
        LocalDateTime created,
        LocalDateTime modified

) {
}
