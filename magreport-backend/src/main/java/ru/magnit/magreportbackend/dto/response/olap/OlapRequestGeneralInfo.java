package ru.magnit.magreportbackend.dto.response.olap;

import java.time.LocalDateTime;
import java.util.Objects;

public record OlapRequestGeneralInfo(
    String userName,
    Long reportJobId,
    Long reportId,
    String reportName,
    String reportJobOwner,
    String requestURL,
    long requestCount,

    LocalDateTime firstDateTime,

    LocalDateTime lastDateTime
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OlapRequestGeneralInfo that = (OlapRequestGeneralInfo) o;

        if (!Objects.equals(userName, that.userName)) return false;
        if (!Objects.equals(reportJobId, that.reportJobId)) return false;
        if (!Objects.equals(reportId, that.reportId)) return false;
        if (!Objects.equals(reportName, that.reportName)) return false;
        if (!Objects.equals(reportJobOwner, that.reportJobOwner))
            return false;
        return Objects.equals(requestURL, that.requestURL);
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (reportJobId != null ? reportJobId.hashCode() : 0);
        result = 31 * result + (reportId != null ? reportId.hashCode() : 0);
        result = 31 * result + (reportName != null ? reportName.hashCode() : 0);
        result = 31 * result + (reportJobOwner != null ? reportJobOwner.hashCode() : 0);
        result = 31 * result + (requestURL != null ? requestURL.hashCode() : 0);
        return result;
    }
}
