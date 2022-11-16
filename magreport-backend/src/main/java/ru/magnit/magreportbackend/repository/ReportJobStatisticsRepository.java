package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.reportjobstats.ReportJobStatistics;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface ReportJobStatisticsRepository extends JpaRepository<ReportJobStatistics, Long> {
    @Query(value = "SELECT DISTINCT RJS.REPORT_JOB_ID," +
        "       RJS.REPORT_ID," +
        "       R.NAME AS reportName," +
        "       U.NAME AS userName " +
        "FROM REPOSITORY.REPORT_JOB_STATISTICS RJS" +
        "         JOIN REPOSITORY.REPORT R ON R.REPORT_ID = RJS.REPORT_ID" +
        "         JOIN REPOSITORY.USERS U ON U.USER_ID = RJS.USER_ID",
        nativeQuery = true)
    List<Tuple> getDistinctBaseStats();

    @Query(value = "SELECT * FROM REPOSITORY.REPORT_JOB_STATISTICS WHERE REPORT_JOB_ID = :reportJobId ORDER BY REPORT_JOB_STATISTICS_ID DESC LIMIT 1",
            nativeQuery = true)
    ReportJobStatistics getLastRecord (@Param("reportJobId") Long reportJobId);

    List<ReportJobStatistics> getAllByReportJobIdOrderById(Long jobId);
}
