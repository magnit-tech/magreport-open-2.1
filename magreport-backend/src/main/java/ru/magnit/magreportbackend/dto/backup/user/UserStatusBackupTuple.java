package ru.magnit.magreportbackend.dto.backup.user;

import java.time.LocalDateTime;

public record UserStatusBackupTuple(

        Long userStatusId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
