package ru.magnit.magreportbackend.mapper.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.Role;
import ru.magnit.magreportbackend.dto.backup.role.RoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class RoleBackupMapper implements Mapper<RoleBackupTuple, Role> {
    @Override
    public RoleBackupTuple from(Role source) {
        return new RoleBackupTuple(
                source.getId(),
                source.getRoleType().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
