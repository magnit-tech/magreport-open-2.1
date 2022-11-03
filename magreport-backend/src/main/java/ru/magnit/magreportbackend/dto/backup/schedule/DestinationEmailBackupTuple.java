package ru.magnit.magreportbackend.dto.backup.schedule;

import java.time.LocalDateTime;

public record DestinationEmailBackupTuple(

        Long destinationEmailId,
        Long scheduleTaskId,
        Long destinationTypeId,
        String val,
        LocalDateTime created,
        LocalDateTime modified

) {}
