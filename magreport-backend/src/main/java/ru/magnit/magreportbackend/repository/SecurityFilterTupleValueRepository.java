package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTupleValue;

public interface SecurityFilterTupleValueRepository extends JpaRepository<SecurityFilterRoleTupleValue, Long> {
}
