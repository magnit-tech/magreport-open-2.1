package ru.magnit.magreportbackend.service.domain;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettings;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettingsJournal;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerSettingSetRequest;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerSettingsJournalRequest;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerSettingsJournalResponse;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerSettingsResponse;
import ru.magnit.magreportbackend.mapper.serversettings.ServerSettingFolderResponseMapper;
import ru.magnit.magreportbackend.repository.ServerSettingsFolderRepository;
import ru.magnit.magreportbackend.repository.ServerSettingsJournalRepository;
import ru.magnit.magreportbackend.repository.ServerSettingsRepository;
import ru.magnit.magreportbackend.service.security.CryptoService;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingsDomainService {

    private final ServerSettingsRepository settingsRepository;
    private final ServerSettingsFolderRepository folderRepository;
    private final ServerSettingsJournalRepository journalRepository;
    private final CryptoService cryptoService;
    private final ServerSettingFolderResponseMapper serverSettingFolderResponseMapper;

    @Transactional
    public ServerSettingsResponse getSettings() {
        final var folders = folderRepository.findAll();
        return new ServerSettingsResponse().setFolders(serverSettingFolderResponseMapper.from(folders));
    }

    @Transactional
    public void setSetting(UserView currentUser, ServerSettingSetRequest request) {

        final var setting = settingsRepository.getReferenceById(request.getSettingId());
        final var oldValue = setting.getValue();
        final var newValue = setting.isEncoded() ? cryptoService.encode(request.getValue()) : request.getValue();

        setting.setValue(newValue);

        settingsRepository.save(setting);


        var journalEntry = new ServerSettingsJournal()
                .setUser(new User(currentUser.getId()))
                .setSetting(new ServerSettings(request.getSettingId()))
                .setValueBefore(oldValue)
                .setValueAfter(newValue);
        journalRepository.save(journalEntry);

    }

    @Transactional
    public List<ServerSettingsJournalResponse> getJournal(ServerSettingsJournalRequest request) {

        List<ServerSettingsJournal> result;

        if (request.getSettingId() == null)
            result = journalRepository.findAll();
        else
            result = journalRepository.findAllBySettingId(request.getSettingId());

        return result
                .stream()
                .map(currentRecord -> new ServerSettingsJournalResponse()
                        .setId(currentRecord.getSetting().getId())
                        .setCode(currentRecord.getSetting().getCode())
                        .setName(currentRecord.getSetting().getName())
                        .setDescription(currentRecord.getSetting().getDescription())
                        .setUser(currentRecord.getUser().getName())
                        .setValueBefore(currentRecord.getValueBefore())
                        .setValueAfter(currentRecord.getValueAfter())
                        .setChangeDate(currentRecord.getCreatedDateTime()))
                .collect(Collectors.toList());
    }

    @Transactional
    public String getValueSetting(String code) {
        ServerSettings setting = settingsRepository.getServerSettingsByCode(code);
        return setting.getValue();
    }


    @Transactional
    public ServerSettingsResponse getSettingsFolder (String codeFolder) {

        final var folder = folderRepository.getServerSettingsFolderByCode(codeFolder);

        return new ServerSettingsResponse().setFolders(
                Collections.singletonList(serverSettingFolderResponseMapper.from(folder))
        );
    }
}
