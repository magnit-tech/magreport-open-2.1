package ru.magnit.magreportbackend.mapper.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.RoleDomainGroup;
import ru.magnit.magreportbackend.dto.backup.role.RoleDomainGroupBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class RoleDomainGroupBackupMapper implements Mapper<RoleDomainGroupBackupTuple, RoleDomainGroup> {
    @Override
    public RoleDomainGroupBackupTuple from(RoleDomainGroup source) {
        return new RoleDomainGroupBackupTuple(
                source.getId(),
                source.getRole().getId(),
                source.getDomainGroup().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
