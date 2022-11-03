package ru.magnit.magreportbackend.dto.response.reportjob;

public record ReportJobBaseStats(
    long reportJobId,
    long reportId,
    String reportName,
    String userName
) {
}
