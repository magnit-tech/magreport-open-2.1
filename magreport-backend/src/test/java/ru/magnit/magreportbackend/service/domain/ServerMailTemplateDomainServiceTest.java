package ru.magnit.magreportbackend.service.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplate;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplateType;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplateTypeEnum;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.request.filterinstance.LikenessType;
import ru.magnit.magreportbackend.dto.request.folder.FolderSearchRequest;
import ru.magnit.magreportbackend.dto.request.serversettings.ServerMailTemplateEditRequest;
import ru.magnit.magreportbackend.dto.response.serversettings.ServerMailTemplateResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.mapper.serversettings.FolderNodeResponseServerMailTemplateTypeMapper;
import ru.magnit.magreportbackend.mapper.serversettings.ServerMailTemplateResponseMapper;
import ru.magnit.magreportbackend.repository.ServerMailTemplateRepository;
import ru.magnit.magreportbackend.repository.ServerMailTemplateTypeRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerMailTemplateDomainServiceTest {

    @Mock
    private ServerMailTemplateRepository repository;
    @Mock
    private ServerMailTemplateTypeRepository serverMailTemplateTypeRepository;
    @Mock
    private ServerMailTemplateResponseMapper mapper;
    @Mock
    private FolderNodeResponseServerMailTemplateTypeMapper folderNodeResponseServerMailTemplateTypeMapper;
    @InjectMocks
    private ServerMailTemplateDomainService service;

    private final Long ID = 1L;
    private final String NAME = "Test";
    private final String DESCRIPTION = "description";
    private final String BODY = "body";
    private final String SUBJECT = "subject";
    private final String CODE = "code";
    private final LocalDateTime CREATED_TIME = LocalDateTime.now();
    private final LocalDateTime MODIFIED_TIME = LocalDateTime.now().plusMinutes(2);


    @Test
    void getServerMailTemplateTest() {

        when(repository.getReferenceById(anyLong())).thenReturn(getServerMailTemplate());
        when(mapper.from((ServerMailTemplate) any())).thenReturn(getServerMailTemplateResponse());

        var result = service.getServerMailTemplate(ID);

        assertEquals(ID, result.getId());
        assertEquals(BODY, result.getBody());
        assertEquals(SUBJECT, result.getSubject());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(ID, result.getUser().getId());
        assertEquals(0, result.getType().getId());
        assertEquals(CODE, result.getCode());
        assertEquals(0, result.getPath().size());
        assertEquals(CREATED_TIME, result.getCreated());
        assertEquals(MODIFIED_TIME, result.getModified());

        verify(repository).getReferenceById(anyLong());
        verify(mapper).from((ServerMailTemplate) any());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void editServerMailTemplateTest() {
        when(repository.getReferenceById(anyLong())).thenReturn(getServerMailTemplate());

        service.editServerMailTemplate(getServerMailTemplateEditRequest(),ID);

        verify(repository).getReferenceById(anyLong());
        verify(repository).save(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getServerMailTemplateByTypeTest() {

        when(serverMailTemplateTypeRepository.getReferenceById(anyLong())).thenReturn(getServerMailTemplateType());
        when(mapper.from(anyList())).thenReturn(Collections.singletonList(getServerMailTemplateResponse()));

        var result = service.getServerMailTemplateByType(ID);

        assertEquals(1 , result.size());

        assertEquals(ID, result.get(0).getId());
        assertEquals(BODY, result.get(0).getBody());
        assertEquals(SUBJECT, result.get(0).getSubject());
        assertEquals(NAME, result.get(0).getName());
        assertEquals(DESCRIPTION, result.get(0).getDescription());
        assertEquals(ID, result.get(0).getUser().getId());
        assertEquals(0, result.get(0).getType().getId());
        assertEquals(CODE, result.get(0).getCode());
        assertEquals(0, result.get(0).getPath().size());
        assertEquals(CREATED_TIME, result.get(0).getCreated());
        assertEquals(MODIFIED_TIME, result.get(0).getModified());

        verify(serverMailTemplateTypeRepository).getReferenceById(ID);
        verify(mapper).from(anyList());
        verifyNoMoreInteractions(serverMailTemplateTypeRepository, mapper);
    }

    @Test
    void getAllServerMailTemplateTest() {
        when(repository.findAll()).thenReturn(Collections.singletonList(getServerMailTemplate()));
        when(mapper.from(anyList())).thenReturn(Collections.singletonList(getServerMailTemplateResponse()));

        var result = service.getAllServerMailTemplate();

        assertEquals(1, result.size());

        assertEquals(ID, result.get(0).getId());
        assertEquals(BODY, result.get(0).getBody());
        assertEquals(SUBJECT, result.get(0).getSubject());
        assertEquals(NAME, result.get(0).getName());
        assertEquals(DESCRIPTION, result.get(0).getDescription());
        assertEquals(ID, result.get(0).getUser().getId());
        assertEquals(0, result.get(0).getType().getId());
        assertEquals(CODE, result.get(0).getCode());
        assertEquals(0, result.get(0).getPath().size());
        assertEquals(CREATED_TIME, result.get(0).getCreated());
        assertEquals(MODIFIED_TIME, result.get(0).getModified());

        verify(repository).findAll();
        verify(mapper).from(anyList());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void searchTemplateTest1() {

        when(serverMailTemplateTypeRepository.getReferenceById(anyLong())).thenReturn(getServerMailTemplateType());

        var result = service.searchTemplate(getFolderSearchRequest(ID));

        assertEquals(1, result.folders().size());
        assertEquals(1, result.objects().size());

        verify(serverMailTemplateTypeRepository).getReferenceById(anyLong());
        verifyNoMoreInteractions(serverMailTemplateTypeRepository);
    }

    @Test
    void searchTemplateTest2() {
        when(serverMailTemplateTypeRepository.findAll()).thenReturn(Collections.singletonList(getServerMailTemplateType()));

        var result = service.searchTemplate(getFolderSearchRequest(null));

        assertEquals(1, result.folders().size());
        assertEquals(1, result.objects().size());

        verify(serverMailTemplateTypeRepository).findAll();
        verifyNoMoreInteractions(serverMailTemplateTypeRepository);

    }


    private ServerMailTemplateResponse getServerMailTemplateResponse(){
        return new ServerMailTemplateResponse()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setBody(BODY)
                .setCode(CODE)
                .setPath(Collections.emptyList())
                .setUser(new UserResponse().setId(ID))
                .setType(ServerMailTemplateTypeEnum.SCHEDULE)
                .setSubject(SUBJECT)
                .setCreated(CREATED_TIME)
                .setModified(MODIFIED_TIME);
    }

    private ServerMailTemplateEditRequest getServerMailTemplateEditRequest(){
        return new ServerMailTemplateEditRequest()
                .setId(ID)
                .setBody(BODY)
                .setSubject(SUBJECT)
                .setName(NAME)
                .setDescription(DESCRIPTION);
    }

    private ServerMailTemplate getServerMailTemplate(){
        return new ServerMailTemplate()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setBody(BODY)
                .setCode(CODE)
                .setSubject(SUBJECT)
                .setUser(new User(ID))
                .setType(new ServerMailTemplateType().setId(ID))
                .setCreatedDateTime(CREATED_TIME)
                .setModifiedDateTime(MODIFIED_TIME);
    }

    private ServerMailTemplateType getServerMailTemplateType(){
        return new ServerMailTemplateType()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setServerMailTemplates(Collections.singletonList(getServerMailTemplate()))
                .setCreatedDateTime(CREATED_TIME)
                .setModifiedDateTime(MODIFIED_TIME);
    }

    private FolderSearchRequest getFolderSearchRequest(Long rootId) {

        return new FolderSearchRequest(
             rootId,
             LikenessType.CONTAINS,
             "",
             true
        );

    }


}
