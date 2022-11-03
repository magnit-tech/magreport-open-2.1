package ru.magnit.magreportbackend.mapper.theme;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.theme.Theme;
import ru.magnit.magreportbackend.dto.backup.serversettings.ThemeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ThemeBackupMapper implements Mapper<ThemeBackupTuple, Theme> {
    @Override
    public ThemeBackupTuple from(Theme source) {
        return new ThemeBackupTuple(
                source.getId(),
                source.getType().getId(),
                source.getUser().getId(),
                source.getFavorite(),
                source.getName(),
                source.getDescription(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime(),
                source.getData()
        );
    }
}
