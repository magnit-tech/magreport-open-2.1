package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterBackupMapper implements Mapper<SecurityFilterBackupTuple, SecurityFilter> {
    @Override
    public SecurityFilterBackupTuple from(SecurityFilter source) {
        return new SecurityFilterBackupTuple(
                source.getId(),
                source.getFilterInstance().getId(),
                source.getOperationType().getId(),
                source.getFolder().getId(),
                source.getUser().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
