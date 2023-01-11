package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.favorite.FavReport;

import java.util.List;

public interface FavReportRepository extends JpaRepository<FavReport, Long> {
    List<FavReport> findAllByUserId(Long userId);
    boolean existsByUserIdAndReportId(Long userId, Long reportId);
    boolean existsByUserIdAndReportIdAndFolderId(Long userId, Long reportId, Long folderId);
    void deleteByUserIdAndReportId (Long userId, Long reportId);
    void deleteByReportId (Long reportId);
}
