package ru.magnit.magreportbackend.dto.request.report;


public record ReportEncryptRequest(

        Long reportId,
        Boolean encrypt
) {
}
