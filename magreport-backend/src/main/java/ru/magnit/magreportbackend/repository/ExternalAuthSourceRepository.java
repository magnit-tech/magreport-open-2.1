package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSource;

public interface ExternalAuthSourceRepository extends JpaRepository<ExternalAuthSource, Long> {
}
