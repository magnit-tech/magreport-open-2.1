package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceType;

public interface ExternalAuthSourceTypeRepository extends JpaRepository<ExternalAuthSourceType, Long> {
}
