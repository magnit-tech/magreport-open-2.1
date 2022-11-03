package ru.magnit.magreportbackend.dto.backup.securityfilter;

import java.time.LocalDateTime;

public record SecurityFilterTupleValueBackupTuple(
        Long securityFilterTupleValueId,
        Long securityFilterTupleId,
        Long filterInstanceFieldId,
        String val,
        LocalDateTime created,
        LocalDateTime modified
) {
}
