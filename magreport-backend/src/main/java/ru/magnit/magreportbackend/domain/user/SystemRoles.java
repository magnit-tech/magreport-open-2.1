package ru.magnit.magreportbackend.domain.user;

public enum SystemRoles {
    ADMIN, DEVELOPER, USER, UNBOUNDED_JOB_ACCESS;

    public Long getId() {
        return (long)ordinal();
    }
}
