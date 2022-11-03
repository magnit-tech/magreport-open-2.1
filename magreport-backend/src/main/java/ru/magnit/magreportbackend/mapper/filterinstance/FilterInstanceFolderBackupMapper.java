package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolder;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceFolderBackupMapper implements Mapper <FilterInstanceFolderBackupTuple, FilterInstanceFolder> {
    @Override
    public FilterInstanceFolderBackupTuple from(FilterInstanceFolder source) {
        return new FilterInstanceFolderBackupTuple(
                source.getId(),
                source.getParentFolder() == null ? null : source.getParentFolder().getId(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
