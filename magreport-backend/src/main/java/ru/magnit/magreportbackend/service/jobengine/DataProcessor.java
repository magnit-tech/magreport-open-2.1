package ru.magnit.magreportbackend.service.jobengine;

import java.sql.SQLException;

@FunctionalInterface
public interface DataProcessor<T> {
    void apply(T t) throws SQLException;
}
