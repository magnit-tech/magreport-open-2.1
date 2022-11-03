package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record DestinationRoleBackupTuple(

        Long destinationRoleId,
        Long scheduleTaskId,
        Long destinationTypeId,
        Long val,
        String name,
        LocalDateTime created,
        LocalDateTime modified

) {
}
