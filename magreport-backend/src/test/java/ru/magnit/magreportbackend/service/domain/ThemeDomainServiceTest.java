package ru.magnit.magreportbackend.service.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.theme.Theme;
import ru.magnit.magreportbackend.domain.theme.ThemeType;
import ru.magnit.magreportbackend.domain.theme.ThemeTypeEnum;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.request.theme.ThemeAddRequest;
import ru.magnit.magreportbackend.dto.response.theme.ThemeResponse;
import ru.magnit.magreportbackend.dto.response.user.UserShortResponse;
import ru.magnit.magreportbackend.mapper.theme.ThemeMapper;
import ru.magnit.magreportbackend.mapper.theme.ThemeMerger;
import ru.magnit.magreportbackend.mapper.theme.ThemeResponseMapper;
import ru.magnit.magreportbackend.repository.ThemeRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ThemeDomainServiceTest {

    private final Long ID = 1L;
    private final String NAME = "Test";
    private final String DESCRIPTION = "description";
    private final Boolean IS_FAVORITE = true;
    private final String DATA = "{}";
    private final LocalDateTime CREATED_TIME = LocalDateTime.now();
    private final LocalDateTime MODIFIED_TIME = LocalDateTime.now().plusMinutes(2);


    @Mock
    private ThemeRepository repository;
    @Mock
    private ThemeMapper mapper;
    @Mock
    private ThemeResponseMapper responseMapper;
    @Mock
    private ThemeMerger merger;

    @InjectMocks
    private ThemeDomainService domainService;


    @Test
    void addThemeTest() {
        when(repository.save(any())).thenReturn(getTheme(IS_FAVORITE));
        when(mapper.from((ThemeAddRequest) any())).thenReturn(getTheme(IS_FAVORITE));
        when(responseMapper.from((Theme) any())).thenReturn(getThemeResponse());

        var result = domainService.addTheme(getThemeAddRequest());

        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(DATA, result.getData());
        assertEquals(ThemeTypeEnum.BLACK, result.getType());
        assertEquals(ID, result.getUser().id());
        assertEquals(IS_FAVORITE, result.getFavorites());
        assertEquals(CREATED_TIME, result.getCreated());
        assertEquals(MODIFIED_TIME, result.getModified());

        verify(repository).save(any());
        verify(repository).clearFavoriteTheme(anyLong());
        verify(mapper).from((ThemeAddRequest) any());
        verify(responseMapper).from((Theme)any());
        verifyNoMoreInteractions(repository, mapper, responseMapper);
    }

    @Test
    void editThemeTest() {

        when(repository.getReferenceById(anyLong())).thenReturn(getTheme(IS_FAVORITE));
        when(merger.merge(any(),any())).thenReturn(getTheme(IS_FAVORITE));
        when(repository.save(any())).thenReturn(getTheme(IS_FAVORITE));
        when(responseMapper.from((Theme) any())).thenReturn(getThemeResponse());

        var result = domainService.editTheme(getThemeAddRequest());

        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(DATA, result.getData());
        assertEquals(ThemeTypeEnum.BLACK, result.getType());
        assertEquals(ID, result.getUser().id());
        assertEquals(IS_FAVORITE, result.getFavorites());
        assertEquals(CREATED_TIME, result.getCreated());
        assertEquals(MODIFIED_TIME, result.getModified());

        verify(repository).getReferenceById(anyLong());
        verify(repository).save(any());
        verify(merger).merge(any(),any());
        verify(responseMapper).from((Theme) any());
        verifyNoMoreInteractions(repository, merger, responseMapper);

    }

    @Test
    void deleteThemeTest() {

        domainService.deleteTheme(ID);

        verify(repository).deleteById(anyLong());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void setFavoriteTest() {

        when(repository.getReferenceById(anyLong())).thenReturn(getTheme(!IS_FAVORITE));

        domainService.setFavorite(ID);

        verify(repository).getReferenceById(anyLong());
        verify(repository).clearFavoriteTheme(anyLong());
        verify(repository).save(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getThemeTest() {

        when(repository.getReferenceById(anyLong())).thenReturn(getTheme(!IS_FAVORITE));
        when(responseMapper.from((Theme) any())).thenReturn(getThemeResponse());

        var result = domainService.getTheme(ID);

        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(DATA, result.getData());
        assertEquals(ThemeTypeEnum.BLACK, result.getType());
        assertEquals(ID, result.getUser().id());
        assertEquals(IS_FAVORITE, result.getFavorites());
        assertEquals(CREATED_TIME, result.getCreated());
        assertEquals(MODIFIED_TIME, result.getModified());

        verify(repository).getReferenceById(anyLong());
        verify(responseMapper).from((Theme) any());
        verifyNoMoreInteractions(repository, responseMapper);
    }

    @Test
    void getAllThemesTest() {

        when(repository.findAll()).thenReturn(Collections.singletonList(getTheme(!IS_FAVORITE)));
        when(responseMapper.from(anyList())).thenReturn(Collections.singletonList(getThemeResponse()));

        var result = domainService.getAllThemes();
        assertFalse(result.isEmpty());
        assertEquals(ID, result.get(0).getId());
        assertEquals(NAME, result.get(0).getName());
        assertEquals(DESCRIPTION, result.get(0).getDescription());
        assertEquals(DATA, result.get(0).getData());
        assertEquals(ThemeTypeEnum.BLACK, result.get(0).getType());
        assertEquals(ID, result.get(0).getUser().id());
        assertEquals(IS_FAVORITE, result.get(0).getFavorites());
        assertEquals(CREATED_TIME, result.get(0).getCreated());
        assertEquals(MODIFIED_TIME, result.get(0).getModified());

        verify(repository).findAll();
        verify(responseMapper).from(anyList());
        verifyNoMoreInteractions(repository,responseMapper);

    }

    @Test
    void getAllUserThemesTest() {


        when(repository.findAllByUserId(anyLong())).thenReturn(Collections.singletonList(getTheme(!IS_FAVORITE)));
        when(responseMapper.from(anyList())).thenReturn(Collections.singletonList(getThemeResponse()));

        var result = domainService.getAllUserThemes(ID);
        assertFalse(result.isEmpty());
        assertEquals(ID, result.get(0).getId());
        assertEquals(NAME, result.get(0).getName());
        assertEquals(DESCRIPTION, result.get(0).getDescription());
        assertEquals(DATA, result.get(0).getData());
        assertEquals(ThemeTypeEnum.BLACK, result.get(0).getType());
        assertEquals(ID, result.get(0).getUser().id());
        assertEquals(IS_FAVORITE, result.get(0).getFavorites());
        assertEquals(CREATED_TIME, result.get(0).getCreated());
        assertEquals(MODIFIED_TIME, result.get(0).getModified());

        verify(repository).findAllByUserId(anyLong());
        verify(responseMapper).from(anyList());
        verifyNoMoreInteractions(repository,responseMapper);

    }


    private ThemeAddRequest getThemeAddRequest() {
        return new ThemeAddRequest()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setTypeId(ID)
                .setUserId(ID)
                .setFavorite(IS_FAVORITE)
                .setData(DATA);
    }

    private ThemeResponse getThemeResponse() {
        return new ThemeResponse(
                ID,
                NAME,
                DESCRIPTION,
                ThemeTypeEnum.BLACK,
                new UserShortResponse(ID,"", ""),
                IS_FAVORITE,
                DATA,
                CREATED_TIME,
                MODIFIED_TIME
        );
    }

    private Theme getTheme(Boolean favorite) {
        return new Theme()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setFavorite(favorite)
                .setType(new ThemeType(ID))
                .setUser(new User(ID))
                .setData(DATA)
                .setCreatedDateTime(CREATED_TIME)
                .setModifiedDateTime(MODIFIED_TIME);
    }

}
