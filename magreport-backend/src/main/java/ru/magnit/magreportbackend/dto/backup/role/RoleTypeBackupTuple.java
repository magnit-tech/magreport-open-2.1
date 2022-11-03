package ru.magnit.magreportbackend.dto.backup.role;

import java.time.LocalDateTime;

public record RoleTypeBackupTuple(
        Long roleTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
