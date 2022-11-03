package ru.magnit.magreportbackend.repository.derivedfield;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.derivedfield.DerivedFieldExpression;

@Repository
public interface DerivedFieldExpressionRepository extends JpaRepository<DerivedFieldExpression, Long> {
    DerivedFieldExpression getByDerivedFieldIdAndParentFieldExpressionIsNull(Long derivedFieldId);
    void deleteAllByDerivedFieldId(Long derivedFieldId);
}
