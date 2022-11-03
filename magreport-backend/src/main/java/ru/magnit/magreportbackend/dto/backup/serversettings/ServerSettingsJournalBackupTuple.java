package ru.magnit.magreportbackend.dto.backup.serversettings;

import java.time.LocalDateTime;

public record ServerSettingsJournalBackupTuple(
        Long serverSettingsJournalId,
        Long userId,
        Long serverSettingsId,
        String valueBefore,
        String valueAfter,
        LocalDateTime created,
        LocalDateTime modified

) {
}
