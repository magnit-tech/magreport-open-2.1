package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterTupleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterTupleBackupMapper implements Mapper<SecurityFilterTupleBackupTuple, SecurityFilterRoleTuple> {
    @Override
    public SecurityFilterTupleBackupTuple from(SecurityFilterRoleTuple source) {
        return new SecurityFilterTupleBackupTuple(
                source.getId(),
                source.getSecurityFilterRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
