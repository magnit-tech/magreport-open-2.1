package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;

import java.util.List;

public interface DataSetFieldRepository extends JpaRepository<DataSetField, Long> {
    List<DataSetField> getDataSetFieldsByDataSetId (Long datasetId);

    void deleteAllByIdIn(List<Long> idList);
}
