package ru.magnit.magreportbackend.mapper.theme;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.theme.Theme;
import ru.magnit.magreportbackend.domain.theme.ThemeType;
import ru.magnit.magreportbackend.dto.backup.serversettings.ThemeBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ThemeRestoreMapping implements Mapper<Theme, ThemeBackupTuple> {
    @Override
    public Theme from(ThemeBackupTuple source) {
        return new Theme()
                .setId(source.themeId())
                .setData(source.data())
                .setName(source.name())
                .setDescription(source.description())
                .setFavorite(source.favorite())
                .setType(new ThemeType(source.themeTypeId()));
    }
}
