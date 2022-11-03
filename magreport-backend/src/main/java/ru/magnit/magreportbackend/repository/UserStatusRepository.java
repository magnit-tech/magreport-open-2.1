package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.user.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
}
