package ru.magnit.magreportbackend.domain.reportjob;

public enum ReportJobStatusEnum {
    SCHEDULED,
    RUNNING,
    COMPLETE,
    FAILED,
    CANCELING,
    CANCELED,
    EXPORT,
    PENDING_DB_CONNECTION;

    public Long getId() {
        return (long)this.ordinal();
    }

    public static ReportJobStatusEnum getById(long id) {
        return ReportJobStatusEnum.values()[(int) id];
    }
}
