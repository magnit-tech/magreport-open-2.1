package ru.magnit.magreportbackend.dto.backup.user;

import java.time.LocalDateTime;

public record UserRoleBackupTuple(
        Long userRoleId,
        Long userId,
        Long roleId,
        Long userRoleTypeId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
