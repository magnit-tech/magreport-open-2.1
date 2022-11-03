package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterTupleBackupTuple(
        Long securityFilterTupleId,
        Long securityFilterRoleId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
