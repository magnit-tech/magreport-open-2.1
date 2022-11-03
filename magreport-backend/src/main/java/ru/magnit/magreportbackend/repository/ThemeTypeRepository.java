package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.theme.ThemeType;

public interface ThemeTypeRepository extends JpaRepository<ThemeType, Long> {
}
