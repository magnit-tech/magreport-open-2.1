package ru.magnit.magreportbackend.mapper.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.RoleType;
import ru.magnit.magreportbackend.dto.backup.role.RoleTypeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class RoleTypeBackupMapper implements Mapper<RoleTypeBackupTuple, RoleType> {
    @Override
    public RoleTypeBackupTuple from(RoleType source) {
        return new RoleTypeBackupTuple(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
