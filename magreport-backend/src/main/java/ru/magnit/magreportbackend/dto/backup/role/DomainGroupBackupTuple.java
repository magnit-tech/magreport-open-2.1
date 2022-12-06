package ru.magnit.magreportbackend.dto.backup.role;

import java.time.LocalDateTime;

public record DomainGroupBackupTuple(

        Long domainGroupId,
        Long domainId,
        String name,
        String description,
        LocalDateTime created,
        LocalDateTime modified

) {
}
