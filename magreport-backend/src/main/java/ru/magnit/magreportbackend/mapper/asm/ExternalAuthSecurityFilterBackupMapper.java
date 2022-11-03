package ru.magnit.magreportbackend.mapper.asm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSecurityFilter;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSecurityFilterBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ExternalAuthSecurityFilterBackupMapper implements Mapper<ExternalAuthSecurityFilterBackupTuple, ExternalAuthSecurityFilter> {
    @Override
    public ExternalAuthSecurityFilterBackupTuple from(ExternalAuthSecurityFilter source) {
        return new ExternalAuthSecurityFilterBackupTuple(
                source.getId(),
                source.getSource().getId(),
                source.getSecurityFilter().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
