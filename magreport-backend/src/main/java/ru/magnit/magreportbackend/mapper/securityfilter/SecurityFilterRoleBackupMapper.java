package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRole;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterRoleBackupMapper implements Mapper<SecurityFilterRoleBackupTuple, SecurityFilterRole> {
    @Override
    public SecurityFilterRoleBackupTuple from(SecurityFilterRole source) {
        return new SecurityFilterRoleBackupTuple(
                source.getId(),
                source.getSecurityFilter().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
