package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterRoleBackupTuple(
        Long securityFilterRoleId,
        Long securityFilterId,
        Long roleId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
