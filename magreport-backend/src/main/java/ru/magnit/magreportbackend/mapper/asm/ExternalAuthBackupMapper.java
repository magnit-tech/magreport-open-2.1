package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuth;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthBackupMapper implements Mapper<ExternalAuthBackupTuple, ExternalAuth> {
    @Override
    public ExternalAuthBackupTuple from(ExternalAuth source) {
        return new ExternalAuthBackupTuple(
                source.getId(),
                source.getRoleType().getId(),
                source.getUser().getId(),
                source.getName(),
                source.getDescription(),
                source.getIsDefaultDomain(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime(),
                source.getIsActive()
        );
    }
}
