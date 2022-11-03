package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.schedule.DestinationType;

public interface DestinationTypeRepository extends JpaRepository<DestinationType, Long> {
}
