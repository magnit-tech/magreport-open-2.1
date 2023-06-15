package ru.magnit.magreportbackend.domain.filterreport;

import ru.magnit.magreportbackend.domain.reportjob.ReportJobUserTypeEnum;

public enum FilterReportOperationTypeEnum  {

    IN_LIST,
    NOT_IN_LIST;

    public Long getId() {
        return (long) this.ordinal();
    }
    public static ReportJobUserTypeEnum getById(long id) {
        return ReportJobUserTypeEnum.values()[(int) id];
    }
}
