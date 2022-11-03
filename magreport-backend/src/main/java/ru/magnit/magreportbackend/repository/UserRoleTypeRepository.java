package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.user.UserRoleType;

public interface UserRoleTypeRepository extends JpaRepository<UserRoleType, Long> {
}
