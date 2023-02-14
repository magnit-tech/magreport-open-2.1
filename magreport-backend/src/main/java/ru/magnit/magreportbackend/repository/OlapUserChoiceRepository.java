package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.olap.OlapUserChoice;

public interface OlapUserChoiceRepository extends JpaRepository<OlapUserChoice, Long> {

    boolean existsByReportIdAndUserId(Long reportId, Long userId);

    OlapUserChoice getOlapUserChoiceByReportIdAndUserId(Long reportId, Long userId);

}
