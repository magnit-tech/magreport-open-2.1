package ru.magnit.magreportbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;
import ru.magnit.magreportbackend.dto.request.folder.FolderRequest;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetFolderResponse;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetResponse;
import ru.magnit.magreportbackend.dto.response.datasource.DataSourceFolderResponse;
import ru.magnit.magreportbackend.dto.response.datasource.DataSourceResponse;
import ru.magnit.magreportbackend.dto.response.exceltemplate.ExcelTemplateFolderResponse;
import ru.magnit.magreportbackend.dto.response.exceltemplate.ExcelTemplateResponse;
import ru.magnit.magreportbackend.dto.response.filterinstance.FilterInstanceFolderResponse;
import ru.magnit.magreportbackend.dto.response.filterinstance.FilterInstanceResponse;
import ru.magnit.magreportbackend.dto.response.filtertemplate.FilterTemplateFolderResponse;
import ru.magnit.magreportbackend.dto.response.filtertemplate.FilterTemplateResponse;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;
import ru.magnit.magreportbackend.dto.response.folderreport.FolderResponse;
import ru.magnit.magreportbackend.dto.response.permission.DataSetFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.DataSourceFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.ExcelTemplateFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FilterInstanceFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FilterTemplateFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionCheckResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.ReportFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.RolePermissionResponse;
import ru.magnit.magreportbackend.dto.response.permission.SecurityFilterFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFolderResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterFolderResponse;
import ru.magnit.magreportbackend.dto.response.securityfilter.SecurityFilterResponse;
import ru.magnit.magreportbackend.service.FolderPermissionsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.DATASET_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.DATASET_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.DATASOURCE_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.DATASOURCE_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.EXCEL_TEMPLATE_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.EXCEL_TEMPLATE_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FILTER_INSTANCE_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FILTER_INSTANCE_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FILTER_TEMPLATE_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FILTER_TEMPLATE_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FOLDER_PERMISSION_CHECK;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.REPORT_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.REPORT_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.SECURITY_FILTER_FOLDER_ADMIN_GET_PERMISSIONS;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.SECURITY_FILTER_FOLDER_ADMIN_SET_PERMISSIONS;
import static ru.magnit.magreportbackend.util.Constant.ISO_DATE_TIME_PATTERN;

@ExtendWith(MockitoExtension.class)
class FolderPermissionControllerTest {

    private static final String USER_NAME = "TestUser";
    private static final Long ID = 1L;
    private static final String NAME = "NAME";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final FolderAuthorityEnum AUTHORITY = FolderAuthorityEnum.WRITE;
    private static final LocalDateTime CREATED = LocalDateTime.now();
    private static final LocalDateTime MODIFIED = LocalDateTime.now().plusMinutes(1);

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DateTimeFormatter formatter;
    @Mock
    private FolderPermissionsService service;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private FolderPermissionsController controller;

    @BeforeEach
    public void initMock() {
        when(authentication.getName()).thenReturn(USER_NAME);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        formatter = DateTimeFormatter.ofPattern(ISO_DATE_TIME_PATTERN);
    }

    @Test
    void getFolderReportPermissions() throws Exception {

        when(service.getFolderReportPermissions(any())).thenReturn(getFolderPermissionsResponse());

       mockMvc
               .perform(post(FOLDER_ADMIN_GET_PERMISSIONS)
                       .accept(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(getFolderRequest()))
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", is(true)))
               .andExpect(jsonPath("$.message", is("")))
               .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
               .andExpect(jsonPath("$.data.folder.name", is(NAME)))
               .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
               .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
               .andExpect(jsonPath("$.data.folder.reports", hasSize(1)))
               .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
               .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
               .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
               .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
               .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));



       verify(service).getFolderReportPermissions(any());
       verifyNoMoreInteractions(service);

    }

    @Test
    void setFolderReportPermissions()  throws Exception {

        when(service.setFolderReportPermissions(any())).thenReturn(getFolderPermissionsResponse());

        mockMvc
                .perform(post(FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.reports", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setFolderReportPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void getReportFolderPermissions() throws Exception{

        when(service.getReportFolderPermissions(any())).thenReturn(getReportFolderPermissionsResponse());

        mockMvc
                .perform(post(REPORT_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.reports", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getReportFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void setReportFolderPermissions() throws Exception{

        when(service.setReportFolderPermissions(any())).thenReturn(getReportFolderPermissionsResponse());

        mockMvc
                .perform(post(REPORT_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.reports", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setReportFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void getDataSourceFolderPermissions() throws Exception {

        when(service.getDataSourceFolderPermissions(any())).thenReturn(getDataSourceFolderPermissionsResponse());

        mockMvc
                .perform(post(DATASOURCE_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.dataSources", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getDataSourceFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void setDataSourceFolderPermissions() throws Exception {

        when(service.setDataSourceFolderPermissions(any())).thenReturn(getDataSourceFolderPermissionsResponse());

        mockMvc
                .perform(post(DATASOURCE_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.dataSources", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setDataSourceFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void getDataSetFolderPermissions() throws Exception{

        when(service.getDataSetFolderPermissions(any())).thenReturn(getDataSetFolderPermissionsResponse());

        mockMvc
                .perform(post(DATASET_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.dataSets", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getDataSetFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void setDataSetFolderPermissions() throws Exception{

        when(service.setDataSetFolderPermissions(any())).thenReturn(getDataSetFolderPermissionsResponse());

        mockMvc
                .perform(post(DATASET_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.dataSets", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setDataSetFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void getExcelTemplateFolderPermissions() throws Exception{
        when(service.getExcelTemplateFolderPermissions(any())).thenReturn(getExcelTemplateFolderPermissionsResponse());

        mockMvc
                .perform(post(EXCEL_TEMPLATE_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.excelTemplates", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getExcelTemplateFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void setExcelTemplateFolderPermissions() throws Exception{

        when(service.setExcelTemplateFolderPermissions(any())).thenReturn(getExcelTemplateFolderPermissionsResponse());

        mockMvc
                .perform(post(EXCEL_TEMPLATE_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.excelTemplates", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setExcelTemplateFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void getFilterInstanceFolderPermissions() throws Exception{

        when(service.getFilterInstanceFolderPermissions(any())).thenReturn(getFilterInstanceFolderPermissionsResponse());

        mockMvc
                .perform(post(FILTER_INSTANCE_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.filterInstances", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getFilterInstanceFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void setFilterInstanceFolderPermissions() throws Exception{

        when(service.setFilterInstanceFolderPermissions(any())).thenReturn(getFilterInstanceFolderPermissionsResponse());

        mockMvc
                .perform(post(FILTER_INSTANCE_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.filterInstances", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setFilterInstanceFolderPermissions(any());
        verifyNoMoreInteractions(service);

    }

    @Test
    void getFilterTemplateFolderPermissions() throws Exception{

        when(service.getFilterTemplateFolderPermissions(any())).thenReturn(getFilterTemplateFolderPermissionsResponse());

        mockMvc
                .perform(post(FILTER_TEMPLATE_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.filterTemplates", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getFilterTemplateFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void setFilterTemplateFolderPermissions() throws Exception {

        when(service.setFilterTemplateFolderPermissions(any())).thenReturn(getFilterTemplateFolderPermissionsResponse());

        mockMvc
                .perform(post(FILTER_TEMPLATE_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.filterTemplates", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setFilterTemplateFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void getSecurityFilterFolderPermissions() throws Exception {

        when(service.getSecurityFilterFolderPermissions(any())).thenReturn(getSecurityFilterFolderPermissionsResponse());

        mockMvc
                .perform(post(SECURITY_FILTER_FOLDER_ADMIN_GET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.securityFilters", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).getSecurityFilterFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void setSecurityFilterFolderPermissions() throws Exception {

        when(service.setSecurityFilterFolderPermissions(any())).thenReturn(getSecurityFilterFolderPermissionsResponse());

        mockMvc
                .perform(post(SECURITY_FILTER_FOLDER_ADMIN_SET_PERMISSIONS)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.folder.id", is(ID.intValue())))
                .andExpect(jsonPath("$.data.folder.name", is(NAME)))
                .andExpect(jsonPath("$.data.folder.description", is(DESCRIPTION)))
                .andExpect(jsonPath("$.data.folder.authority", is(AUTHORITY.name())))
                .andExpect(jsonPath("$.data.folder.securityFilters", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.childFolders", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.path", hasSize(1)))
                .andExpect(jsonPath("$.data.folder.created", is(CREATED.format(formatter))))
                .andExpect(jsonPath("$.data.folder.modified", is(MODIFIED.format(formatter))))
                .andExpect(jsonPath("$.data.rolePermissions", hasSize(1)));

        verify(service).setSecurityFilterFolderPermissions(any());
        verifyNoMoreInteractions(service);
    }

    @Test
    void folderPermissionCheck() throws Exception{

        when(service.checkFolderPermission(any())).thenReturn(getFolderPermissionCheckResponse());

        mockMvc
                .perform(post(FOLDER_PERMISSION_CHECK)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getFolderRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.authority", is(AUTHORITY.name())))
        ;

        verify(service).checkFolderPermission(any());
        verifyNoMoreInteractions(service);

    }


    private FolderPermissionsResponse getFolderPermissionsResponse(){
        return new FolderPermissionsResponse(
                new FolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new FolderResponse()))
                        .setReports(Collections.singletonList(new ReportResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                       null,
                       null
                ))
        );
    }

    private ReportFolderPermissionsResponse getReportFolderPermissionsResponse(){
        return new ReportFolderPermissionsResponse(
                new ReportFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new ReportFolderResponse()))
                        .setReports(Collections.singletonList(new ReportResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))
        );
    }

    private DataSourceFolderPermissionsResponse getDataSourceFolderPermissionsResponse(){
        return new DataSourceFolderPermissionsResponse(
                new DataSourceFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new DataSourceFolderResponse()))
                        .setDataSources(Collections.singletonList(new DataSourceResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))
        );
    }

    private DataSetFolderPermissionsResponse getDataSetFolderPermissionsResponse (){
        return new DataSetFolderPermissionsResponse(
                new DataSetFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new DataSetFolderResponse()))
                        .setDataSets(Collections.singletonList(new DataSetResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))
        );
    }

    private ExcelTemplateFolderPermissionsResponse getExcelTemplateFolderPermissionsResponse() {
        return new ExcelTemplateFolderPermissionsResponse (
                new ExcelTemplateFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new ExcelTemplateFolderResponse()))
                        .setExcelTemplates(Collections.singletonList(new ExcelTemplateResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))
        );
    }

    private FilterInstanceFolderPermissionsResponse getFilterInstanceFolderPermissionsResponse(){
        return new FilterInstanceFolderPermissionsResponse(
                new FilterInstanceFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new FilterInstanceFolderResponse()))
                        .setFilterInstances(Collections.singletonList(new FilterInstanceResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))

        );
    }

    private FilterTemplateFolderPermissionsResponse getFilterTemplateFolderPermissionsResponse(){
        return new FilterTemplateFolderPermissionsResponse(
                new FilterTemplateFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new FilterTemplateFolderResponse()))
                        .setFilterTemplates(Collections.singletonList(new FilterTemplateResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))
        );
    }

    private SecurityFilterFolderPermissionsResponse getSecurityFilterFolderPermissionsResponse(){
        return new SecurityFilterFolderPermissionsResponse(
                new SecurityFilterFolderResponse()
                        .setId(ID)
                        .setParentId(ID)
                        .setName(NAME)
                        .setDescription(DESCRIPTION)
                        .setAuthority(AUTHORITY)
                        .setChildFolders(Collections.singletonList(new SecurityFilterFolderResponse()))
                        .setSecurityFilters(Collections.singletonList(new SecurityFilterResponse()))
                        .setPath(Collections.singletonList(new FolderNodeResponse(ID,ID,NAME,DESCRIPTION,CREATED,MODIFIED)))
                        .setCreated(CREATED)
                        .setModified(MODIFIED),
                Collections.singletonList(new RolePermissionResponse(
                        null,
                        null
                ))
        );
    }

    private FolderPermissionCheckResponse getFolderPermissionCheckResponse(){
        return new FolderPermissionCheckResponse(FolderAuthorityEnum.WRITE);
    }

    private FolderRequest getFolderRequest(){
        return new FolderRequest();
    }
}
