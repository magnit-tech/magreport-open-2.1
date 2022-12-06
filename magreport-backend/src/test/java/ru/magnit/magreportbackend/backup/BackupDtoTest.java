package ru.magnit.magreportbackend.backup;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.magnit.magreportbackend.domain.asm.ExternalAuth;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSecurityFilter;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSource;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceField;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceFieldFilterInstanceField;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceFieldType;
import ru.magnit.magreportbackend.domain.asm.ExternalAuthSourceType;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolder;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRole;
import ru.magnit.magreportbackend.domain.dataset.DataSetFolderRolePermission;
import ru.magnit.magreportbackend.domain.datasource.DataSource;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolder;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolderRole;
import ru.magnit.magreportbackend.domain.datasource.DataSourceFolderRolePermission;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplate;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolder;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolderRole;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplateFolderRolePermission;
import ru.magnit.magreportbackend.domain.excel.ReportExcelTemplate;
import ru.magnit.magreportbackend.domain.excel.UserReportExcelTemplate;
import ru.magnit.magreportbackend.domain.favorite.FavReport;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolder;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolderRole;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceFolderRolePermission;
import ru.magnit.magreportbackend.domain.filterreport.FilterReport;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportField;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportGroup;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplate;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateField;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolder;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolderRole;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateFolderRolePermission;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterType;
import ru.magnit.magreportbackend.domain.folderreport.Folder;
import ru.magnit.magreportbackend.domain.folderreport.FolderReport;
import ru.magnit.magreportbackend.domain.folderreport.FolderRole;
import ru.magnit.magreportbackend.domain.folderreport.FolderRolePermission;
import ru.magnit.magreportbackend.domain.olap.OlapConfiguration;
import ru.magnit.magreportbackend.domain.olap.ReportOlapConfiguration;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.report.ReportField;
import ru.magnit.magreportbackend.domain.report.ReportFolder;
import ru.magnit.magreportbackend.domain.report.ReportFolderRole;
import ru.magnit.magreportbackend.domain.report.ReportFolderRolePermission;
import ru.magnit.magreportbackend.domain.reportjob.JobToken;
import ru.magnit.magreportbackend.domain.reportjob.ReportJob;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobFilter;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTuple;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobTupleField;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobUser;
import ru.magnit.magreportbackend.domain.reportjobstats.ReportJobStatistics;
import ru.magnit.magreportbackend.domain.schedule.DestinationEmail;
import ru.magnit.magreportbackend.domain.schedule.DestinationRole;
import ru.magnit.magreportbackend.domain.schedule.DestinationType;
import ru.magnit.magreportbackend.domain.schedule.DestinationUser;
import ru.magnit.magreportbackend.domain.schedule.Schedule;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTask;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilter;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSet;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterDataSetField;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolder;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRole;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterFolderRolePermission;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRole;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTuple;
import ru.magnit.magreportbackend.domain.securityfilter.SecurityFilterRoleTupleValue;
import ru.magnit.magreportbackend.domain.serversettings.ServerMailTemplate;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettings;
import ru.magnit.magreportbackend.domain.serversettings.ServerSettingsJournal;
import ru.magnit.magreportbackend.domain.theme.Theme;
import ru.magnit.magreportbackend.domain.user.DomainGroup;
import ru.magnit.magreportbackend.domain.user.Role;
import ru.magnit.magreportbackend.domain.user.RoleDomainGroup;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.domain.user.UserRole;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateBackupTuple;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.exceltemplate.ExcelTemplateFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSecurityFilterBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceFieldFIFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceFieldTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.external.ExternalAuthSourceTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterinstance.FilterInstanceFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterReportGroupBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filterreport.FilterTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.filtertemplate.FilterTemplateFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.olap.OlapConfigurationBackupTuple;
import ru.magnit.magreportbackend.dto.backup.olap.ReportOlapConfigurationBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.FavReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportExcelTemplateBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobBackupTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobFilterBackupTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobStatisticsBackupTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobTupleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobTupleFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobUserBackupTuple;
import ru.magnit.magreportbackend.dto.backup.role.DomainGroupBackupTuple;
import ru.magnit.magreportbackend.dto.backup.role.RoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.role.RoleDomainGroupBackupTuple;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationEmailBackupTuple;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.schedule.DestinationUserBackupTuple;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleTaskBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterDatasetBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterDatasetFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterFolderRolePermissionBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterTupleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.securityfilter.SecurityFilterTupleValueBackupTuple;
import ru.magnit.magreportbackend.dto.backup.serversettings.JobTokenBackupTuple;
import ru.magnit.magreportbackend.dto.backup.serversettings.ServerMailTemplateBackupTuple;
import ru.magnit.magreportbackend.dto.backup.serversettings.ServerSettingsBackupTuple;
import ru.magnit.magreportbackend.dto.backup.serversettings.ServerSettingsJournalBackupTuple;
import ru.magnit.magreportbackend.dto.backup.serversettings.ThemeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.user.UserReportExcelTemplateBackupTuple;
import ru.magnit.magreportbackend.dto.backup.user.UserRoleBackupTuple;
import ru.magnit.magreportbackend.dto.backup.user.UsersBackupTuple;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BackupDtoTest {


    @Test
    void testDataSource() {
        checkNumberOfFields(DataSource.class, DataSourceBackupTuple.class);
    }
    
    @Test
    void testDataSourceFolder() {
        checkNumberOfFields(DataSourceFolder.class, DataSourceFolderBackupTuple.class);
    }

    @Test
    void testDataSourceFolderRole() {
        checkNumberOfFields(DataSourceFolderRole.class, DataSourceFolderRoleBackupTuple.class);
    }

    @Test
    void testDataSourceFolderRolePermission() {
        checkNumberOfFields(DataSourceFolderRolePermission.class, DataSourceFolderRolePermissionBackupTuple.class);
    }

    @Test
    void testDataSet() {
        checkNumberOfFields(DataSet.class, DataSetBackupTuple.class);
    }

    @Test
    void testDataSetField() {
        checkNumberOfFields(DataSetField.class, DataSetFieldBackupTuple.class);
    }

    @Test
    void testDataSetFolder() {
        checkNumberOfFields(DataSetFolder.class, DataSetFolderBackupTuple.class);
    }

    @Test
    void testDataSetFolderRole() {
        checkNumberOfFields(DataSetFolderRole.class, DataSetFolderRoleBackupTuple.class);
    }

    @Test
    void testDataSetFolderRolePermission() {
        checkNumberOfFields(DataSetFolderRolePermission.class, DataSetFolderRolePermissionBackupTuple.class);
    }

    @Test
    void testExternalAuth() {
        checkNumberOfFields(ExternalAuth.class, ExternalAuthBackupTuple.class);
    }

    @Test
    void testExternalAuthSecurityFilter() {
        checkNumberOfFields(ExternalAuthSecurityFilter.class, ExternalAuthSecurityFilterBackupTuple.class);
    }

    @Test
    void testExternalAuthSource() {
        checkNumberOfFields(ExternalAuthSource.class, ExternalAuthSourceBackupTuple.class);
    }

    @Test
    void testExternalAuthSourceField() {
        checkNumberOfFields(ExternalAuthSourceField.class, ExternalAuthSourceFieldBackupTuple.class);
    }

    @Test
    void testExternalAuthSourceFieldFilterInstanceField() {
        checkNumberOfFields(ExternalAuthSourceFieldFilterInstanceField.class, ExternalAuthSourceFieldFIFieldBackupTuple.class);
    }

    @Test
    void testExternalAuthSourceFieldType() {
        checkNumberOfFields(ExternalAuthSourceFieldType.class, ExternalAuthSourceFieldTypeBackupTuple.class);
    }

    @Test
    void testExternalAuthSourceType() {
        checkNumberOfFields(ExternalAuthSourceType.class, ExternalAuthSourceTypeBackupTuple.class);
    }

    @Test
    void testExcelTemplate() {
        checkNumberOfFields(ExcelTemplate.class, ExcelTemplateBackupTuple.class);
    }

    @Test
    void testExcelTemplateFolder() {
        checkNumberOfFields(ExcelTemplateFolder.class, ExcelTemplateFolderBackupTuple.class);
    }

    @Test
    void testExcelTemplateFolderRole() {
        checkNumberOfFields(ExcelTemplateFolderRole.class, ExcelTemplateFolderRoleBackupTuple.class);
    }

    @Test
    void testExcelTemplateFolderRolePermission() {
        checkNumberOfFields(ExcelTemplateFolderRolePermission.class, ExcelTemplateFolderRolePermissionBackupTuple.class);
    }

    @Test
    void testFilterInstance() {
        checkNumberOfFields(FilterInstance.class, FilterInstanceBackupTuple.class);
    }

    @Test
    void testFilterInstanceField() {
        checkNumberOfFields(FilterInstanceField.class, FilterInstanceFieldBackupTuple.class);
    }

    @Test
    void testFilterInstanceFolder() {
        checkNumberOfFields(FilterInstanceFolder.class, FilterInstanceFolderBackupTuple.class);
    }
    @Test
    void testFilterInstanceFolderRole() {
        checkNumberOfFields(FilterInstanceFolderRole.class, FilterInstanceFolderRoleBackupTuple.class);
    }
    @Test
    void testFilterInstanceFolderRolePermission() {
        checkNumberOfFields(FilterInstanceFolderRolePermission.class, FilterInstanceFolderRolePermissionBackupTuple.class);
    }
    @Test
    void testFilterReport() {
        checkNumberOfFields(FilterReport.class, FilterReportBackupTuple.class);
    }
    @Test
    void testFilterReportField() {
        checkNumberOfFields(FilterReportField.class, FilterReportFieldBackupTuple.class);
    }
    @Test
    void testFilterReportGroup() {
        checkNumberOfFields(FilterReportGroup.class, FilterReportGroupBackupTuple.class);
    }
    @Test
    void testFilterTemplate() {
        checkNumberOfFields(FilterTemplate.class, FilterTemplateBackupTuple.class);
    }
    @Test
    void testFilterTemplateField() {
        checkNumberOfFields(FilterTemplateField.class, FilterTemplateFieldBackupTuple.class);
    }
    @Test
    void testFilterTemplateFolder() {
        checkNumberOfFields(FilterTemplateFolder.class, FilterTemplateFolderBackupTuple.class);
    }
    @Test
    void testFilterTemplateFolderRole() {
        checkNumberOfFields(FilterTemplateFolderRole.class, FilterTemplateFolderRoleBackupTuple.class);
    }
    @Test
    void testFilterTemplateFolderRolePermission() {
        checkNumberOfFields(FilterTemplateFolderRolePermission.class, FilterTemplateFolderRolePermissionBackupTuple.class);
    }
    @Test
    void testFilterType() {
        checkNumberOfFields(FilterType.class, FilterTypeBackupTuple.class);
    }
    @Test
    void testFolder() {
        checkNumberOfFields(Folder.class, FolderBackupTuple.class);
    }

    @Test
    void testFolderReport() {
        checkNumberOfFields(FolderReport.class, FolderReportBackupTuple.class);
    }

    @Test
    void testFolderRole() {
        checkNumberOfFields(FolderRole.class, FolderRoleBackupTuple.class);
    }

    @Test
    void testFolderRolePermission() {
        checkNumberOfFields(FolderRolePermission.class, FolderRolePermissionBackupTuple.class);
    }

    @Test
    void testOlapConfiguration() {
        checkNumberOfFields(OlapConfiguration.class, OlapConfigurationBackupTuple.class);
    }
    @Test
    void testReportOlapConfiguration() {
        checkNumberOfFields(ReportOlapConfiguration.class, ReportOlapConfigurationBackupTuple.class);
    }

    @Test
    void testFavReport() {
        checkNumberOfFields(FavReport.class, FavReportBackupTuple.class);
    }

    @Test
    void testReport() {
        checkNumberOfFields(Report.class, ReportBackupTuple.class);
    }

    @Test
    void testReportExcelTemplate() {
        checkNumberOfFields(ReportExcelTemplate.class, ReportExcelTemplateBackupTuple.class);
    }

    @Test
    void testReportField() {
        checkNumberOfFields(ReportField.class, ReportFieldBackupTuple.class);
    }

    @Test
    void testReportFolder() {
        checkNumberOfFields(ReportFolder.class, ReportFolderBackupTuple.class);
    }

    @Test
    void testReportFolderRole() {
        checkNumberOfFields(ReportFolderRole.class, ReportFolderRoleBackupTuple.class);
    }

    @Test
    void testReportFolderRolePermission() {
        checkNumberOfFields(ReportFolderRolePermission.class, ReportFolderRolePermissionBackupTuple.class);
    }

    @Test
    void testReportJob() {
        checkNumberOfFields(ReportJob.class, ReportJobBackupTuple.class);
    }

    @Test
    void testReportJobFilter() {
        checkNumberOfFields(ReportJobFilter.class, ReportJobFilterBackupTuple.class);
    }

    @Test
    void testReportJobTuple() {
        checkNumberOfFields(ReportJobTuple.class, ReportJobTupleBackupTuple.class);
    }

    @Test
    void testReportJobTupleField() {
        checkNumberOfFields(ReportJobTupleField.class, ReportJobTupleFieldBackupTuple.class);
    }

    @Test
    void testReportJobUser() {
        checkNumberOfFields(ReportJobUser.class, ReportJobUserBackupTuple.class);
    }

    @Test
    void testReportJobStatistics() {
        checkNumberOfFields(ReportJobStatistics.class, ReportJobStatisticsBackupTuple.class);
    }

    @Test
    void testRole() {
        checkNumberOfFields(Role.class, RoleBackupTuple.class);
    }

    @Test
    void testRoleDomainGroup() {
        checkNumberOfFields(RoleDomainGroup.class, RoleDomainGroupBackupTuple.class);
    }

    @Test
    void testDestinationEmail() {
        checkNumberOfFields(DestinationEmail.class, DestinationEmailBackupTuple.class);
    }

    @Test
    void testDestinationRole() {
        checkNumberOfFields(DestinationRole.class, DestinationRoleBackupTuple.class);
    }

    @Test
    void testDestinationType() {
        checkNumberOfFields(DestinationType.class, DestinationTypeBackupTuple.class);
    }

    @Test
    void testDestinationUser() {
        checkNumberOfFields(DestinationUser.class, DestinationUserBackupTuple.class);
    }

    @Test
    void testSchedule() {
        checkNumberOfFields(Schedule.class, ScheduleBackupTuple.class);
    }

    @Test
    void testScheduleTask() {
        checkNumberOfFields(ScheduleTask.class, ScheduleTaskBackupTuple.class);
    }

    @Test
    void testSecurityFilter() {
        checkNumberOfFields(SecurityFilter.class, SecurityFilterBackupTuple.class);
    }

    @Test
    void testSecurityFilterDataset() {
        checkNumberOfFields(SecurityFilterDataSet.class, SecurityFilterDatasetBackupTuple.class);
    }

    @Test
    void testSecurityFilterDatasetField() {
        checkNumberOfFields(SecurityFilterDataSetField.class, SecurityFilterDatasetFieldBackupTuple.class);
    }

    @Test
    void testSecurityFilterFolder() {
        checkNumberOfFields(SecurityFilterFolder.class, SecurityFilterFolderBackupTuple.class);
    }

    @Test
    void testSecurityFilterFolderRole() {
        checkNumberOfFields(SecurityFilterFolderRole.class, SecurityFilterFolderRoleBackupTuple.class);
    }

    @Test
    void testSecurityFilterFolderRolePermission() {
        checkNumberOfFields(SecurityFilterFolderRolePermission.class, SecurityFilterFolderRolePermissionBackupTuple.class);
    }

    @Test
    void testSecurityFilterRole() {
        checkNumberOfFields(SecurityFilterRole.class, SecurityFilterRoleBackupTuple.class);
    }

    @Test
    void testSecurityFilterTuple() {
        checkNumberOfFields(SecurityFilterRoleTuple.class, SecurityFilterTupleBackupTuple.class);
    }

    @Test
    void testSecurityFilterTupleValue() {
        checkNumberOfFields(SecurityFilterRoleTupleValue.class, SecurityFilterTupleValueBackupTuple.class);
    }

    @Test
    void testJobToken() {
        checkNumberOfFields(JobToken.class, JobTokenBackupTuple.class);
    }

    @Test
    void testServerMailTemplate() {
        checkNumberOfFields(ServerMailTemplate.class, ServerMailTemplateBackupTuple.class);
    }

    @Test
    void testServerSettings() {
        checkNumberOfFields(ServerSettings.class, ServerSettingsBackupTuple.class);
    }

    @Test
    void testServerSettingsJournal() {
        checkNumberOfFields(ServerSettingsJournal.class, ServerSettingsJournalBackupTuple.class);
    }

    @Test
    void testTheme() {
        checkNumberOfFields(Theme.class, ThemeBackupTuple.class);
    }

    @Test
    void testDomainGroup() {
        checkNumberOfFields(DomainGroup.class, DomainGroupBackupTuple.class);
    }
    @Test
    void testUsers() {
        checkNumberOfFields(User.class, UsersBackupTuple.class);
    }

    @Test
    void testUserReportExcelTemplate() {
        checkNumberOfFields(UserReportExcelTemplate.class, UserReportExcelTemplateBackupTuple.class);
    }

    @Test
    void testUserRole() {
        checkNumberOfFields(UserRole.class, UserRoleBackupTuple.class);
    }

    private  void checkNumberOfFields(Class<?> parentClass, Class<?> dtoClass)  {
        var x = getAllClassFields(parentClass);
        var y = getAllClassFields(dtoClass);

        assertEquals(getAllClassFields(parentClass).size(), getAllClassFields(dtoClass).size(), "Number of fields in class " + parentClass.getSimpleName() + " changed.");
    }


    private List<Field> getAllClassFields(Class<?> clazz) {
        var fields = new LinkedList<Field>();
        var isSuperClass = new AtomicBoolean(false);

        while (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> !isSuperClass.get() || (Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())))
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .filter(field -> !field.getType().getName().equals("java.util.List"))
                    .toList()
            );
            clazz = clazz.getSuperclass();
            isSuperClass.set(true);
        }

        return fields;
    }
}
