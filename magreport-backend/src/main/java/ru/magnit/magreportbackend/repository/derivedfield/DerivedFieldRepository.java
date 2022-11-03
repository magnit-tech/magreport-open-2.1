package ru.magnit.magreportbackend.repository.derivedfield;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedField;

@Repository
public interface DerivedFieldRepository extends JpaRepository<DerivedField, Long> {
}
