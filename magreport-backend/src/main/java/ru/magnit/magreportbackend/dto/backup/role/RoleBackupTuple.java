package ru.magnit.magreportbackend.dto.backup.role;

import java.time.LocalDateTime;

public record RoleBackupTuple(
        Long roleId,
        Long roleTypeId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified
) {
}
