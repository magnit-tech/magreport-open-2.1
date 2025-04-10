package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record DestinationUserBackupTuple(

        Long destinationUserId,
        Long scheduleTaskId,
        Long destinationTypeId,

        Long userId,
        String val,
        String domain,
        LocalDateTime created,
        LocalDateTime modified

) {
}
