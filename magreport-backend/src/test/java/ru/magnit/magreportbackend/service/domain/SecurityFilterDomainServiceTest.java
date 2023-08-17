package ru.magnit.magreportbackend.service.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.dataset.DataType;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterOperationType;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterOperationTypeEnum;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplate;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterType;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTypeEnum;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthority;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolder;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRole;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRolePermission;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRole;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTuple;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTupleValue;
import ru.magnit.magreportbackend.domain.user.Role;
import ru.magnit.magreportbackend.dto.inner.UserView;
import ru.magnit.magreportbackend.dto.inner.filter.FilterData;
import ru.magnit.magreportbackend.dto.inner.securityfilter.SecurityFilterFieldMapping;
import ru.magnit.magreportbackend.dto.request.ChangeParentFolderRequest;
import ru.magnit.magreportbackend.dto.request.filterinstance.ListValuesCheckRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderAddRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderChangeParentRequest;
import ru.magnit.magreportbackend.dto.request.folder.FolderRenameRequest;
import ru.magnit.magreportbackend.dto.request.securityfilter.SecurityFilterAddRequest;
import ru.magnit.magreportbackend.dto.request.securityfilter.SecurityFilterSetRoleRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.RoleSettingsResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterFolderResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterRoleSettingsResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterShortResponse;
import ru.magnit.magreportbackend.dto.response.user.RoleResponse;
import ru.magnit.magreportbackend.dto.tuple.TupleValue;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterDataFIMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.FolderNodeResponseSecurityFilterFolderMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterDataMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterFolderMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterFolderResponseMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterFolderRolePermissionMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterMerger;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterResponseMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterRoleMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterRoleSettingsResponseMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterShortResponseMapper;
import ru.magnit.magreportbackend.repository.SecurityFilterDataSetFieldRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterDataSetRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterFolderRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterFolderRoleRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterRoleRepository;
import ru.magnit.magreportbackend.repository.custom.SecurityFilterReportFieldsMappingRepository;
import ru.magnit.magreportbackend.service.jobengine.filter.FilterQueryExecutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum.WRITE;
import static ru.magnit.magreportbackend.dto.backup.BackupObjectTypeEnum.SECURITY_FILTER;

@ExtendWith(MockitoExtension.class)
class SecurityFilterDomainServiceTest {

    @Mock
    private SecurityFilterRepository repository;

    @Mock
    private SecurityFilterRoleRepository roleRepository;

    @Mock
    private SecurityFilterFolderRepository folderRepository;

    @Mock
    private SecurityFilterDataSetRepository dataSetRepository;

    @Mock
    private SecurityFilterDataSetFieldRepository dataSetFieldRepository;

    @Mock
    private SecurityFilterFolderRoleRepository folderRoleRepository;

    @Mock
    private SecurityFilterReportFieldsMappingRepository fieldsMappingRepository;

    @Mock
    private FilterQueryExecutor queryExecutor;

    @Mock
    private SecurityFilterMapper securityFilterMapper;

    @Mock
    private SecurityFilterResponseMapper securityFilterResponseMapper;

    @Mock
    private SecurityFilterFolderResponseMapper securityFilterFolderResponseMapper;

    @Mock
    private SecurityFilterFolderMapper securityFilterFolderMapper;

    @Mock
    private SecurityFilterRoleMapper securityFilterRoleMapper;

    @Mock
    private SecurityFilterRoleSettingsResponseMapper securityFilterRoleSettingsResponseMapper;

    @Mock
    private FilterDataFIMapper filterDataFIMapper;

    @Mock
    private SecurityFilterMerger securityFilterMerger;

    @Mock
    private FolderNodeResponseSecurityFilterFolderMapper folderNodeResponseSecurityFilterFolderMapper;

    @Mock
    private SecurityFilterFolderRolePermissionMapper securityFilterFolderRolePermissionMapper;

    @Mock
    private SecurityFilterShortResponseMapper securityFilterShortResponseMapper;

    @Mock
    private SecurityFilterDataMapper securityFilterDataMapper;

    @InjectMocks
    SecurityFilterDomainService domainService;

    private final static Long ID = 1L;
    private final static String NAME = "name";
    private final static String DESCRIPTION = "description";
    private final static LocalDateTime CREATED = LocalDateTime.now();
    private final static LocalDateTime MODIFIED = LocalDateTime.now().plusDays(1);

    @Test
    void addSecurityFilter() {
        when(securityFilterMapper.from(any(SecurityFilterAddRequest.class))).thenReturn(getFilter());
        when(repository.save(any())).thenReturn(getFilter());

        assertNotNull(domainService.addSecurityFilter(new UserView().setId(ID), getSecurityFilterAddRequest()));

        verify(securityFilterMapper).from(any(SecurityFilterAddRequest.class));
        verify(repository).save(any());

        verifyNoMoreInteractions(securityFilterMapper, repository);
    }

    @Test
    void getSecurityFilter() {
        when(securityFilterResponseMapper.from(any(SecurityFilter.class))).thenReturn(getSecurityFilterResponse());
        when(repository.getReferenceById(any())).thenReturn(getFilter());
        when(folderRepository.existsById(anyLong())).thenReturn(true);
        when(folderRepository.getReferenceById(any())).thenReturn(new SecurityFilterFolder());

        assertNotNull(domainService.getSecurityFilter(ID));

        verify(securityFilterResponseMapper).from(any(SecurityFilter.class));
        verify(repository).getReferenceById(any());
        verifyNoMoreInteractions(securityFilterResponseMapper, repository);
    }

    @Test
    void deleteSecurityFilter() {

        domainService.deleteSecurityFilter(ID);

        verify(repository).deleteById(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteRoles() {

        domainService.deleteRoles(ID);

        verify(roleRepository).deleteBySecurityFilterId(any());
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void getFolder() {
        when(folderRepository.getAllByParentFolderIsNull()).thenReturn(Collections.singletonList(new SecurityFilterFolder()));
        assertNotNull(domainService.getFolder(null));

        verify(folderRepository).getAllByParentFolderIsNull();

        verifyNoMoreInteractions(folderRepository);


        when(securityFilterFolderResponseMapper.from(any(SecurityFilterFolder.class))).thenReturn(new SecurityFilterFolderResponse());
        when(folderRepository.getReferenceById(any())).thenReturn(new SecurityFilterFolder());
        when(folderRepository.existsById(any())).thenReturn(true);

        assertNotNull(domainService.getFolder(ID));

        verify(folderRepository, times(2)).existsById(any());
        verify(folderRepository, times(2)).getReferenceById(any());

        verifyNoMoreInteractions(folderRepository);

        Mockito.reset(folderRepository);

        when(folderRepository.existsById(any())).thenReturn(false);

        assertThrows(InvalidParametersException.class, () -> domainService.getFolder(ID));

        verify(folderRepository).existsById(any());
        verifyNoMoreInteractions(folderRepository);

    }

    @Test
    void addFolder() {
        when(securityFilterFolderMapper.from(any(FolderAddRequest.class))).thenReturn(new SecurityFilterFolder());
        when(folderRepository.save(any())).thenReturn(new SecurityFilterFolder().setId(ID));

        ReflectionTestUtils.setField(domainService, "maxLevel", 128L);

        assertNotNull(domainService.addFolder(new FolderAddRequest()));

        verify(securityFilterFolderMapper).from(any(FolderAddRequest.class));
        verify(folderRepository).save(any());
    }

    @Test
    void renameFolder() {

        when(folderRepository.getReferenceById(any())).thenReturn(new SecurityFilterFolder());
        when(folderRepository.existsById(any())).thenReturn(true);

        assertNotNull(domainService.renameFolder(new FolderRenameRequest().setId(ID)));

        verify(folderRepository).getReferenceById(any());
        verify(folderRepository).existsById(any());
        verify(folderRepository).save(any());
    }

    @Test
    void deleteFolder() {

        when(folderRepository.existsById(any())).thenReturn(true);
        when(folderRepository.existsByParentFolderId(any())).thenReturn(false);
        when(repository.existsByFolderId(any())).thenReturn(false);

        domainService.deleteFolder(ID);

        verify(folderRepository).existsById(any());
        verify(folderRepository).existsByParentFolderId(any());
        verify(folderRepository).deleteById(any());
        verify(repository).existsByFolderId(any());

        verifyNoMoreInteractions(folderRepository, repository);

        Mockito.reset(folderRepository);

        when(folderRepository.existsById(any())).thenReturn(true);
        when(folderRepository.existsByParentFolderId(any())).thenReturn(true);

        assertThrows(InvalidParametersException.class, () -> domainService.deleteFolder(ID));

        verify(folderRepository).existsByParentFolderId(any());
        verify(folderRepository).existsById(any());
        verifyNoMoreInteractions(folderRepository);
    }

    @Test
    void setRoleSettings() {

        when(securityFilterRoleMapper.from(anyList())).thenReturn(Collections.emptyList());

        domainService.setRoleSettings(new SecurityFilterSetRoleRequest());

        verify(securityFilterRoleMapper).from(anyList());
        verifyNoMoreInteractions(securityFilterRoleMapper);
    }

    @Test
    void getFilterRoleSettings() {

        when(repository.getReferenceById(any())).thenReturn(getFilter());
        when(filterDataFIMapper.from(any(FilterInstance.class))).thenReturn(getFilterData());
        when(securityFilterRoleSettingsResponseMapper.from(any(SecurityFilter.class)))
                .thenReturn(getSecurityFilterRoleSettingsResponse());
        when(queryExecutor.getFieldsValues(any())).thenReturn(Collections.emptyList());

        assertNotNull(domainService.getFilterRoleSettings(ID));

        verify(repository).getReferenceById(any());
        verify(filterDataFIMapper).from(any(FilterInstance.class));
        verify(securityFilterRoleSettingsResponseMapper).from(any(SecurityFilter.class));
        verify(queryExecutor).getFieldsValues(any());

        verifyNoMoreInteractions(repository, filterDataFIMapper, securityFilterRoleSettingsResponseMapper, queryExecutor);
    }

    @Test
    void deleteDataSetMappings() {

        domainService.deleteDataSetMappings(ID);

        verify(dataSetRepository).deleteAllBySecurityFilterId(any());
        verify(dataSetFieldRepository).deleteAllBySecurityFilterId(any());

        verifyNoMoreInteractions(dataSetRepository, dataSetFieldRepository);
    }

    @Test
    void editSecurityFilter() {
        when(repository.getReferenceById(any())).thenReturn(getFilter());
        when(securityFilterMerger.merge(any(), any())).thenReturn(getFilter());

        domainService.editSecurityFilter(getSecurityFilterAddRequest());

        verify(repository).getReferenceById(any());
        verify(securityFilterMerger).merge(any(), any());
        verify(repository).save(any());

        verifyNoMoreInteractions(repository, securityFilterMerger);
    }

    @Test
    void getEffectiveSettings() {
        when(fieldsMappingRepository.getFieldsMappings(any())).thenReturn(Collections.emptyList());

        assertNotNull(domainService.getEffectiveSettingsForReport(ID, Collections.emptySet()));

        verify(fieldsMappingRepository).getFieldsMappings(any());

        verifyNoMoreInteractions(fieldsMappingRepository);
    }

    @Test
    void getEffectiveSettingsTest() {
        when(fieldsMappingRepository.getFieldsMappings(any())).thenReturn(Collections.singletonList(getSecurityFilterFieldMapping()));
        when(repository.getAllByIdIn(anyList())).thenReturn(Collections.singletonList(getFilter()));

        assertNotNull(domainService.getEffectiveSettingsForReport(ID, Set.of(ID)));

        verify(fieldsMappingRepository).getFieldsMappings(any());

        verifyNoMoreInteractions(fieldsMappingRepository);
    }

    @Test
    void deleteRole() {

        domainService.deleteRole(ID, ID);
        verify(roleRepository).deleteAllBySecurityFilterIdAndRoleId(any(), any());
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void changeParentFolder() {
        when(folderRepository.getReferenceById(any())).thenReturn(new SecurityFilterFolder());
        when(securityFilterFolderResponseMapper.from(any(SecurityFilterFolder.class))).thenReturn(new SecurityFilterFolderResponse());

        assertNotNull(domainService.changeParentFolder(new FolderChangeParentRequest().setId(ID).setParentId(ID)));

        verify(folderRepository).getReferenceById(any());
        verify(securityFilterFolderResponseMapper).from(any(SecurityFilterFolder.class));
        verify(folderRepository).save(any());

        verifyNoMoreInteractions(folderRepository, securityFilterFolderResponseMapper);

    }

    @Test
    void getPathSecurityFilter() {
        when(repository.getReferenceById(any())).thenReturn(getFilter());
        when(folderNodeResponseSecurityFilterFolderMapper.from(any(SecurityFilterFolder.class))).thenReturn(new FolderNodeResponse(ID, ID, "", "", LocalDateTime.now(), LocalDateTime.now()));

        assertNotNull(domainService.getPathSecurityFilter(ID));

        verify(repository).getReferenceById(any());
        verify(folderNodeResponseSecurityFilterFolderMapper, times(2)).from(any(SecurityFilterFolder.class));

        verifyNoMoreInteractions(repository, folderNodeResponseSecurityFilterFolderMapper);
    }

    @Test
    void checkSecurityFilterExistsForDataset() {
        when(dataSetRepository.findAllByDataSetId(any())).thenReturn(Collections.emptyList());

        assertNotNull(domainService.checkSecurityFilterExistsForDataset(ID));

        verify(dataSetRepository).findAllByDataSetId(any());
        verifyNoMoreInteractions(dataSetFieldRepository);
    }

    @Test
    void getFoldersPermittedToRoleTest() {

        when(folderRoleRepository.getAllByRoleId(anyLong())).thenReturn(Collections.singletonList(getSecurityFilterFolderRole()));

        var result = domainService.getFoldersPermittedToRole(ID);

        assertEquals(1, result.size());

        assertEquals(ID, result.get(0).folderId());
        assertEquals(NAME, result.get(0).folderName());
        assertEquals(WRITE, result.get(0).roleAuthority());
        assertEquals(SECURITY_FILTER.name(), result.get(0).typeFolder().name());

        verify(folderRoleRepository).getAllByRoleId(anyLong());
        verifyNoMoreInteractions(folderRoleRepository);

    }

    @Test
    void addFolderPermittedToRoleTest() {
        when(securityFilterFolderRolePermissionMapper.from(anyList())).thenReturn(Collections.singletonList(new SecurityFilterFolderRolePermission(ID)));

        domainService.addFolderPermittedToRole(Collections.singletonList(ID), ID, Collections.singletonList(WRITE));

        verify(securityFilterFolderRolePermissionMapper).from(anyList());
        verify(folderRoleRepository).saveAll(anyList());
        verifyNoMoreInteractions(folderRoleRepository);
    }

    @Test
    void deleteFolderPermittedToRoleTest() {

        domainService.deleteFolderPermittedToRole(Collections.singletonList(ID), ID);

        verify(folderRoleRepository).deleteAllByFolderIdInAndRoleId(anyList(), anyLong());
        verifyNoMoreInteractions(folderRoleRepository);
    }

    @Test
    void getFiltersWithSettingsForRoleTest() {

        when(roleRepository.getAllByRoleId(anyLong())).thenReturn(Collections.singletonList(getSecurityFilterRole()));
        when(securityFilterShortResponseMapper.from(anyList())).thenReturn(Collections.singletonList(getSecurityFilterShortResponse()));

        var result = domainService.getFiltersWithSettingsForRole(ID);

        assertFalse(result.isEmpty());
        assertEquals(ID, result.get(0).id());
        assertEquals(NAME, result.get(0).name());
        assertEquals(DESCRIPTION, result.get(0).description());
        assertEquals("", result.get(0).userName());
        assertEquals(CREATED, result.get(0).created());
        assertEquals(MODIFIED, result.get(0).modified());
        assertTrue(result.get(0).path().isEmpty());

        verify(roleRepository).getAllByRoleId(anyLong());
        verify(securityFilterShortResponseMapper).from(anyList());
        verifyNoMoreInteractions(roleRepository, securityFilterShortResponseMapper);

    }

    @Test
    void addFolderExceptionTest() {

        when(securityFilterFolderMapper.from(any(FolderAddRequest.class))).thenReturn(new SecurityFilterFolder());
        when(folderRepository.save(any())).thenReturn(new SecurityFilterFolder().setId(ID));

        ReflectionTestUtils.setField(domainService, "maxLevel", 0L);

        var request = new FolderAddRequest();

        assertThrows(InvalidParametersException.class, () -> domainService.addFolder(request));

        verify(securityFilterFolderMapper).from(any(FolderAddRequest.class));
        verify(folderRepository).save(any());
    }

    @Test
    void getEffectiveSettingsForFilterTest() {

        when(repository.getAllByDataSetIdsAndRoleIds(any(),any())).thenReturn(Collections.singletonList(getFilter()));

        var result = domainService.getEffectiveSettingsForFilter(ID, Set.of(ID));

        verify(repository).getAllByDataSetIdsAndRoleIds(any(),any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getFolderIdsTest() {

        when(repository.getAllByIdIn(anyList())).thenReturn(Collections.singletonList(getFilter()));

        var result = domainService.getFolderIds(Collections.singletonList(ID));

        assertFalse(result.isEmpty());
        assertEquals(ID, result.get(0));

        verify(repository).getAllByIdIn(anyList());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void changeFilterInstanceParentFolderTest() {

        when(repository.getAllByIdIn(anyList())).thenReturn(Collections.singletonList(getFilter()));

        domainService.changeFilterInstanceParentFolder(new ChangeParentFolderRequest().setDestFolderId(ID).setObjIds(Collections.emptyList()));

        verify(repository).getAllByIdIn(anyList());
        verify(repository).saveAll(anyList());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void checkFilterReportValuesTest() {

        when(repository.getReferenceById(anyLong())).thenReturn(getFilter());
        when(queryExecutor.getFieldsValues(any())).thenReturn(Collections.emptyList());
        when(securityFilterDataMapper.from((SecurityFilter) any())).thenReturn(getFilterData());
        when(securityFilterResponseMapper.from((SecurityFilter) any())).thenReturn(getSecurityFilterResponse());

        var result = domainService.checkFilterReportValues(getListValuesCheckRequest());

        assertEquals(ID, result.filter().getId());
        assertFalse(result.values().isEmpty());

        verify(repository).getReferenceById(anyLong());
        verify(queryExecutor).getFieldsValues(any());
        verifyNoMoreInteractions(repository, queryExecutor);
    }

    @Test
    void getReportFieldsMappingTest() {

        when(fieldsMappingRepository.getFieldsMappings(anyLong())).thenReturn(Collections.singletonList(getSecurityFilterFieldMapping()));

        var result = domainService.getReportFieldsMapping(ID);

        assertTrue(result.containsKey(ID));
        assertTrue(result.containsValue("name"));

        verify(fieldsMappingRepository).getFieldsMappings(anyLong());
        verifyNoMoreInteractions(fieldsMappingRepository);
    }


    private SecurityFilter getFilter() {
        return new SecurityFilter()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setCreatedDateTime(CREATED)
                .setModifiedDateTime(CREATED)
                .setFolder(new SecurityFilterFolder().setId(ID).setParentFolder(new SecurityFilterFolder()))
                .setFilterInstance(new FilterInstance().setId(ID))
                .setFilterRoles(
                        Collections.singletonList(
                                new SecurityFilterRole(ID)
                                        .setRole(new Role(ID))
                                        .setSecurityFilter(new SecurityFilter(ID)
                                                .setFilterInstance(
                                                        new FilterInstance(ID)
                                                                .setFilterTemplate(
                                                                        new FilterTemplate(ID)
                                                                                .setType(new FilterType().setId(ID))
                                                                )
                                                )
                                                .setOperationType(new FilterOperationType(FilterOperationTypeEnum.IS_EQUAL))
                                        )
                                        .setFilterRoleTuples(Collections.singletonList(
                                                new SecurityFilterRoleTuple(ID)
                                                        .setTupleValues(Collections.singletonList(
                                                                new SecurityFilterRoleTupleValue(ID)
                                                                        .setField(new FilterInstanceField(ID)
                                                                                .setDataSetField(new DataSetField(ID)
                                                                                        .setType(new DataType(DataTypeEnum.DATE))
                                                                                )
                                                                        )
                                                        ))
                                        ))
                        ));

    }

    private SecurityFilterAddRequest getSecurityFilterAddRequest() {
        return new SecurityFilterAddRequest()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setFolderId(ID);
    }

    private SecurityFilterResponse getSecurityFilterResponse() {
        return new SecurityFilterResponse(
                ID,
                ID,
                null,
                FilterOperationTypeEnum.IS_BETWEEN,
                NAME,
                DESCRIPTION,
                new ArrayList<>(),
                new ArrayList<>(),
                "",
                CREATED,
                CREATED,
                new ArrayList<>()
        );
    }

    private FilterData getFilterData() {
        return new FilterData(
                null,
                ID,
                FilterTypeEnum.TOKEN_INPUT,
                "schema",
                "tableName",
                NAME,
                "code",
                DESCRIPTION,
                new ArrayList<>()
        );
    }

    private SecurityFilterRoleSettingsResponse getSecurityFilterRoleSettingsResponse() {
        return new SecurityFilterRoleSettingsResponse(
                ID,
                Collections.singletonList(new RoleSettingsResponse(
                        new RoleResponse(),
                        Collections.emptyList()
                ))
        );
    }

    private SecurityFilterFolderRole getSecurityFilterFolderRole() {
        return new SecurityFilterFolderRole()
                .setId(ID)
                .setRole(new Role(ID))
                .setFolder(new SecurityFilterFolder(ID).setName(NAME))
                .setPermissions(Collections.singletonList(
                        new SecurityFilterFolderRolePermission(ID)
                                .setAuthority(new FolderAuthority(WRITE))
                ))
                .setCreatedDateTime(CREATED)
                .setModifiedDateTime(MODIFIED);
    }

    private SecurityFilterRole getSecurityFilterRole() {
        return new SecurityFilterRole()
                .setId(ID)
                .setSecurityFilter(new SecurityFilter(ID))
                .setRole(new Role(ID))
                .setCreatedDateTime(CREATED)
                .setModifiedDateTime(MODIFIED);

    }


    private SecurityFilterShortResponse getSecurityFilterShortResponse() {
        return new SecurityFilterShortResponse(
                ID,
                NAME,
                DESCRIPTION,
                "",
                CREATED,
                MODIFIED,
                Collections.emptyList()
        );
    }

    private ListValuesCheckRequest getListValuesCheckRequest() {
        return new ListValuesCheckRequest()
                .setFilterId(ID)
                .setValues(Collections.singletonList(new TupleValue(ID, "")));

    }

    private SecurityFilterFieldMapping getSecurityFilterFieldMapping() {
        return new SecurityFilterFieldMapping(
                ID,
                ID,
                ID,
                NAME
        );
    }
}
