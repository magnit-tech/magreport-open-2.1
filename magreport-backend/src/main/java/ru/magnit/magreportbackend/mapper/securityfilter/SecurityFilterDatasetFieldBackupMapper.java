package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSetField;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterDatasetFieldBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterDatasetFieldBackupMapper implements Mapper<SecurityFilterDatasetFieldBackupTuple, SecurityFilterDataSetField> {
    @Override
    public SecurityFilterDatasetFieldBackupTuple from(SecurityFilterDataSetField source) {
        return new SecurityFilterDatasetFieldBackupTuple(
                source.getId(),
                source.getSecurityFilter().getId(),
                source.getFilterInstanceField().getId(),
                source.getDataSetField().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
