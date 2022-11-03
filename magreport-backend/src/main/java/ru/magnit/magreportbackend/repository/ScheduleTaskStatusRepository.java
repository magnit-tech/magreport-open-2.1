package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTaskStatus;

public interface ScheduleTaskStatusRepository extends JpaRepository<ScheduleTaskStatus, Long> {
}
