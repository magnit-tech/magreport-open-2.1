package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceField;

public interface ExternalAuthSourceFieldRepository extends JpaRepository<ExternalAuthSourceField, Long> {
}
