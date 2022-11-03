package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSet;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterDatasetBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterDatasetBackupMapper implements Mapper<SecurityFilterDatasetBackupTuple, SecurityFilterDataSet> {
    @Override
    public SecurityFilterDatasetBackupTuple from(SecurityFilterDataSet source) {
        return new SecurityFilterDatasetBackupTuple(
                source.getId(),
                source.getSecurityFilter().getId(),
                source.getDataSet().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
