package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.excel.UserReportExcelTemplate;

public interface UserReportExcelTemplateRepository extends JpaRepository<UserReportExcelTemplate, Long> {
}
