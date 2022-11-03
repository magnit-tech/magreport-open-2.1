package ru.magnit.magreportbackend.dto.backup.olap;

import java.time.LocalDateTime;

public record ReportOlapConfigurationBackupTuple(
        Long reportOlapConfigurationId,
        Long reportId,
        Long userId,
        Long reportJobId,
        Long olapConfigurationId,
        Long creatorId,
        boolean isDefault,
        boolean isShared,
        boolean isCurrent,
        LocalDateTime created,
        LocalDateTime modified
) {
}
