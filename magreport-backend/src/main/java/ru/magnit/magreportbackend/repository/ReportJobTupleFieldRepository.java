package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTupleField;

public interface ReportJobTupleFieldRepository extends JpaRepository<ReportJobTupleField, Long> {
}
