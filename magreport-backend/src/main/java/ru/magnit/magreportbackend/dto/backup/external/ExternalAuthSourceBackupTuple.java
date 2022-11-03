package ru.magnit.magreportbackend.dto.backup.external;

import java.time.LocalDateTime;

public record ExternalAuthSourceBackupTuple(

        Long externalAuthSourceId,
        Long externalAuthId,
        Long datasetId,
        Long externalAuthSourceTypeId,
        String preSql,
        String postSql,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
