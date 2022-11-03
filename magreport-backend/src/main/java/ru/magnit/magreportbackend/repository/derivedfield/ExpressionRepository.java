package ru.magnit.magreportbackend.repository.derivedfield;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.derivedfield.Expression;

@Repository
public interface ExpressionRepository extends JpaRepository<Expression, Long> {
}
