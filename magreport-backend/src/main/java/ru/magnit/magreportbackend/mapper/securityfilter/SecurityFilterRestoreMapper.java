package ru.magnit.magreportbackend.mapper.securityfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterOperationType;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolder;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class SecurityFilterRestoreMapper implements Mapper<SecurityFilter, SecurityFilterBackupTuple> {
    @Override
    public SecurityFilter from(SecurityFilterBackupTuple source) {
        return new SecurityFilter()
                .setId(source.securityFilterId())
                .setName(source.name())
                .setDescription(source.description())
                .setFolder(new SecurityFilterFolder(source.securityFilterFolderId()))
                .setFilterInstance(new FilterInstance(source.filterInstanceId()))
                .setOperationType(new FilterOperationType(source.filterOperationTypeId()));
    }
}
