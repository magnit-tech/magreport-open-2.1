package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceFieldType;

public interface ExternalAuthSourceFieldTypeRepository extends JpaRepository<ExternalAuthSourceFieldType, Long> {
}
