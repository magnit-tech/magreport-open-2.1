package ru.magnit.magreportbackend.domain.schedule;

public enum ScheduleTypeEnum {

    EVERY_DAY,
    EVERY_WEEK,
    EVERY_MONTH,
    DAY_END_MONTH,
    WEEK_MONTH,
    WEEK_END_MONTH,
    MANUAL,
    EVERY_N_MINUTES;

    public Long getId() {
        return (long) this.ordinal();
    }

    public static ScheduleTypeEnum getById(long id) {
        return ScheduleTypeEnum.values()[(int) id];
    }
}
