package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import ru.magnit.magreportbackend.domain.event.Event;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    void deleteAllByCreatedDateTimeBefore(LocalDateTime lastDateTime);

}
