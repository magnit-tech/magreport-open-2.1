package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportField;

public interface FilterReportFieldRepository extends JpaRepository<FilterReportField, Long> {
}
