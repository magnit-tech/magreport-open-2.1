package ru.magnit.magreportbackend.repository.derivedfield;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedField;

import java.util.List;

@Repository
public interface DerivedFieldRepository extends JpaRepository<DerivedField, Long> {

    List<DerivedField> getAllByReportId(Long reportId);
}
