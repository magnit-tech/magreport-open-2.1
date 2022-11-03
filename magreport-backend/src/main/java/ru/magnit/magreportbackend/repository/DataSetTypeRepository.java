package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.domain.dataset.DataSetType;

import java.util.List;

@Repository
public interface DataSetTypeRepository extends JpaRepository<DataSetType, Long> {
}
