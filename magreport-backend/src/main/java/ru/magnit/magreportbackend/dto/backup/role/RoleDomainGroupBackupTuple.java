package ru.magnit.magreportbackend.dto.backup.role;

import java.time.LocalDateTime;

public record RoleDomainGroupBackupTuple(
        Long roleDomainGroupId,
        Long roleId,
        Long domainGroupId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
