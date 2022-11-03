package ru.magnit.magreportbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSetField;

import java.util.List;

public interface SecurityFilterDataSetFieldRepository extends JpaRepository<SecurityFilterDataSetField, Long> {
    void deleteAllBySecurityFilterId(Long securityFilterId);

    @Query(nativeQuery = true, value = "SELECT SFDF.* \n" +
        "FROM REPOSITORY.SECURITY_FILTER_DATASET_FIELD SFDF \n" +
        "INNER JOIN REPOSITORY.DATASET_FIELD DF ON DF.DATASET_FIELD_ID = SFDF.DATASET_FIELD_ID AND DF.DATASET_ID=:dataSetId\n" +
        "WHERE SFDF.SECURITY_FILTER_ID=:securityFilterId")
    List<SecurityFilterDataSetField> getAllBySecurityFilterIdAndDataSetId(@Param("securityFilterId") Long securityFilterId, @Param("dataSetId") Long dataSetId);
}
