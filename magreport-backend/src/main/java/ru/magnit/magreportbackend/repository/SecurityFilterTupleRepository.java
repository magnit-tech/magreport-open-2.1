package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTuple;

public interface SecurityFilterTupleRepository extends JpaRepository<SecurityFilterRoleTuple, Long> {
}
