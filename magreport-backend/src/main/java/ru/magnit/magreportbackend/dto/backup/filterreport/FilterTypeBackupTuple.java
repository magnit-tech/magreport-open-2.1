package ru.magnit.magreportbackend.dto.backup.filterreport;

import java.time.LocalDateTime;

public record FilterTypeBackupTuple(

        Long filterTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
