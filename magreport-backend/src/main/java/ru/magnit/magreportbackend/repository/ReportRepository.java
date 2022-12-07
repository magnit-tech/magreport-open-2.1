package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.magnit.magreportbackend.domain.report.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByFolderId(Long id);
    List<Report> findByDataSetId (Long dataSetId);
    List<Report> getAllByIdIn(List<Long> ids);
    @Query(value = "SELECT DISTINCT REP FROM REPORT REP INNER join REPORT_JOB RJ on REP=RJ.report")
    List<Report> findAllScheduledReports();
}
