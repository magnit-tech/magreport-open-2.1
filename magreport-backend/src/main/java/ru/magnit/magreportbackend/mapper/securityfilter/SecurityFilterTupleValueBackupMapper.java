package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTupleValue;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterTupleValueBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterTupleValueBackupMapper implements Mapper<SecurityFilterTupleValueBackupTuple, SecurityFilterRoleTupleValue> {
    @Override
    public SecurityFilterTupleValueBackupTuple from(SecurityFilterRoleTupleValue source) {
        return new SecurityFilterTupleValueBackupTuple(
                source.getId(),
                source.getTuple().getId(),
                source.getField().getId(),
                source.getValue(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
