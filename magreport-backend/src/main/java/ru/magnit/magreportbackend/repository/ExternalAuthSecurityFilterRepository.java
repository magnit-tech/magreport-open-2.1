package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSecurityFilter;

public interface ExternalAuthSecurityFilterRepository extends JpaRepository<ExternalAuthSecurityFilter, Long> {
}
