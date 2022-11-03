package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTaskType;

public interface ScheduleTaskTypeRepository extends JpaRepository<ScheduleTaskType, Long> {
}
