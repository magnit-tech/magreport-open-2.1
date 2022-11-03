package ru.magnit.magreportbackend.dto.backup.user;

import java.time.LocalDateTime;

public record UserRoleTypeBackupTuple(
        Long userRoleTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
