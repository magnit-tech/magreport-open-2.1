package ru.magnit.magreportbackend.mapper.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.Role;
import ru.magnit.magreportbackend.domain.user.RoleType;
import ru.magnit.magreportbackend.dto.backup.role.RoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class RoleRestoreMapper implements Mapper<Role, RoleBackupTuple> {
    @Override
    public Role from(RoleBackupTuple source) {
        return new Role()
                .setId(source.roleId())
                .setRoleType(new RoleType(source.roleTypeId()))
                .setName(source.name())
                .setDescription(source.description());
    }
}
