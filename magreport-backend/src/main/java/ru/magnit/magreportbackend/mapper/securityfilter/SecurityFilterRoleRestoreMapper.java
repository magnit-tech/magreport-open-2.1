package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRole;
import ru.magnit.magreportbackend.domain.user.Role;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterRoleRestoreMapper implements Mapper <SecurityFilterRole, SecurityFilterRoleBackupTuple> {
    @Override
    public SecurityFilterRole from(SecurityFilterRoleBackupTuple source) {
        return new SecurityFilterRole()
                .setId(source.securityFilterRoleId())
                .setSecurityFilter(new SecurityFilter(source.securityFilterId()))
                .setRole(new Role(source.roleId()));
    }
}
