package ru.magnit.magreportbackend.mapper.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.user.DomainGroup;
import ru.magnit.magreportbackend.dto.backup.role.DomainGroupBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class DomainGroupBackupMapper implements Mapper<DomainGroupBackupTuple, DomainGroup> {
    @Override
    public DomainGroupBackupTuple from(DomainGroup source) {
        return new DomainGroupBackupTuple(
                source.getId(),
                source.getDomain().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
