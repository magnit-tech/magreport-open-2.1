package ru.magnit.magreportbackend.repository.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.magnit.magreportbackend.dto.inner.securityfilter.SecurityFilterFieldMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SecurityFilterReportFieldsMappingRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    @SuppressWarnings("unchecked")
    public List<SecurityFilterFieldMapping> getFieldsMappings(Long reportId) {
        return entityManager.createNativeQuery("""
            SELECT\s
             RSFFLS.REPORT_ID,\s
             RSFFLS.SECURITY_FILTER_ID,
             RSFFLS.FILTER_INSTANCE_FIELD_ID,
             MIN(RFFL.REPORT_DATASET_FIELD_NAME) AS FIELD_NAME\s
            FROM REPOSITORY.V_REPORT_SECURITY_FILTER_FIELD_LNK RSFFLS
            INNER JOIN REPOSITORY.V_REPORT_SECURITY_FILTER_FIELD_LNK  RSFFLW ON RSFFLW.REPORT_ID = RSFFLS.REPORT_ID AND RSFFLW.FI_DATASET_FIELD_ID = RSFFLS.FI_DATASET_FIELD_ID\s
            LEFT JOIN REPOSITORY.V_REPORT_FILTER_FIELD_LNK  RFFL ON RFFL.REPORT_ID = RSFFLS.REPORT_ID AND RFFL.FILTER_DATASET_FIELD_ID IN (RSFFLS.SF_DATASET_FIELD_ID, RSFFLS.FI_DATASET_FIELD_ID, RSFFLW.SF_DATASET_FIELD_ID)
            WHERE RSFFLS.REPORT_ID = ?1
            GROUP BY\s
             RSFFLS.REPORT_ID,\s
             RSFFLS.SECURITY_FILTER_ID,
             RSFFLS.FILTER_INSTANCE_FIELD_ID;
            """)
            .setParameter(1, reportId)
            .getResultStream()
            .map(entry -> new SecurityFilterFieldMapping(
                ((Integer)((Object[])entry)[0]).longValue(),
                ((Integer)((Object[])entry)[1]).longValue(),
                ((Integer)((Object[])entry)[2]).longValue(),
                ((String)((Object[])entry)[3])
            ))
            .toList();
    }
}
