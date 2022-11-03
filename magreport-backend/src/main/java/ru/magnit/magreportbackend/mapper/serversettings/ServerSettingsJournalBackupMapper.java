package ru.magnit.magreportbackend.mapper.serversettings;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettingsJournal;
import ru.magnit.magreportbackend.dto.backup.serversettings.ServerSettingsJournalBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ServerSettingsJournalBackupMapper implements Mapper<ServerSettingsJournalBackupTuple, ServerSettingsJournal> {
    @Override
    public ServerSettingsJournalBackupTuple from(ServerSettingsJournal source) {
        return new ServerSettingsJournalBackupTuple(
                source.getId(),
                source.getUser().getId(),
                source.getSetting().getId(),
                source.getValueBefore(),
                source.getValueAfter(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
