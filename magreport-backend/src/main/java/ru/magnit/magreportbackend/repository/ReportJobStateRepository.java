package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobState;

public interface ReportJobStateRepository extends JpaRepository<ReportJobState, Long> {
}
