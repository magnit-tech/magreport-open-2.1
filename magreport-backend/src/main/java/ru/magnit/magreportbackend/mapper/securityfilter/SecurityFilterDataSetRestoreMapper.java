package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSet;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterDatasetBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterDataSetRestoreMapper implements Mapper<SecurityFilterDataSet, SecurityFilterDatasetBackupTuple> {
    @Override
    public SecurityFilterDataSet from(SecurityFilterDatasetBackupTuple source) {
        return new SecurityFilterDataSet()
                .setId(source.securityFilterDatasetId())
                .setSecurityFilter(new SecurityFilter(source.securityFilterId()))
                .setDataSet(new DataSet(source.dataSetId()));
    }
}
