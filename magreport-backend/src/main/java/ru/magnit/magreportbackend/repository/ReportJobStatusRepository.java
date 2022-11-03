package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobStatus;

public interface ReportJobStatusRepository extends JpaRepository<ReportJobStatus,Long> {
}
