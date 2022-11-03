package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolderRole;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderRoleBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceFolderRoleBackupMapper implements Mapper<FilterInstanceFolderRoleBackupTuple, FilterInstanceFolderRole> {
    @Override
    public FilterInstanceFolderRoleBackupTuple from(FilterInstanceFolderRole source) {
        return new FilterInstanceFolderRoleBackupTuple(
                source.getId(),
                source.getFolder().getId(),
                source.getRole().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
