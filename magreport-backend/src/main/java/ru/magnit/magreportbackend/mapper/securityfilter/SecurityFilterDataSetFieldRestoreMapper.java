package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSetField;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterDatasetFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterDataSetFieldRestoreMapper implements Mapper<SecurityFilterDataSetField, SecurityFilterDatasetFieldBackupTuple> {
    @Override
    public SecurityFilterDataSetField from(SecurityFilterDatasetFieldBackupTuple source) {
        return new SecurityFilterDataSetField()
                .setId(source.securityFilterDataSetFieldId())
                .setSecurityFilter(new SecurityFilter(source.securityFilterId()))
                .setDataSetField(new DataSetField(source.datasetFieldId()))
                .setFilterInstanceField(new FilterInstanceField(source.filterInstanceFieldId()));
    }
}
