package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTuple;

public interface ReportJobTupleRepository extends JpaRepository<ReportJobTuple, Long> {
}
