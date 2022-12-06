package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolder;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FilterInstanceFolderRestoreMapper implements Mapper<FilterInstanceFolder, FilterInstanceFolderBackupTuple> {
    @Override
    public FilterInstanceFolder from(FilterInstanceFolderBackupTuple source) {
        return new FilterInstanceFolder()
                .setId(source.filterInstanceFolderId())
                .setParentFolder( new FilterInstanceFolder(source.parentId()))
                .setName(source.name())
                .setDescription(source.description());
    }
}
