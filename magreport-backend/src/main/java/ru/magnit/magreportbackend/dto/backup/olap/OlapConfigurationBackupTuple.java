package ru.magnit.magreportbackend.dto.backup.olap;

import java.time.LocalDateTime;

public record OlapConfigurationBackupTuple(

        Long olapConfigurationId,
        Long userId,
        String data,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
