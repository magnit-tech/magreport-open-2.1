package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record DestinationTypeBackupTuple (
        Long destinationTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
