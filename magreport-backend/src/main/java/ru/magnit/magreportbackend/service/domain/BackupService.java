package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.data.Json;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.backup.BackupRequest;
import ru.magnit.magreportbackend.exception.FileSystemException;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthBackupMapper;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthSecurityFilterBackupMapper;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthSourceBackupMapper;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthSourceFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthSourceFieldFiFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthSourceFieldTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.asm.ExternalAuthSourceTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.auth.DomainGroupBackupMapper;
import ru.magnit.magreportbackend.mapper.auth.UserReportExcelTemplateBackupMapper;
import ru.magnit.magreportbackend.mapper.auth.UserRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.auth.UsersBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.exceltemplate.ExcelTemplateBackupMapper;
import ru.magnit.magreportbackend.mapper.exceltemplate.ExcelTemplateFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.exceltemplate.ExcelTemplateFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.exceltemplate.ExcelTemplateFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterInstanceBackupMapper;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterInstanceFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterInstanceFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterInstanceFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterInstanceFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.filterreport.FilterReportBackupMapper;
import ru.magnit.magreportbackend.mapper.filterreport.FilterReportFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.filterreport.FilterReportGroupBackupMapper;
import ru.magnit.magreportbackend.mapper.filtertemplate.FilterTemplateBackupMapper;
import ru.magnit.magreportbackend.mapper.filtertemplate.FilterTemplateFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.filtertemplate.FilterTemplateFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.filtertemplate.FilterTemplateFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.filtertemplate.FilterTemplateFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.filtertemplate.FilterTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.folderreport.FolderBackupMapper;
import ru.magnit.magreportbackend.mapper.folderreport.FolderReportBackupMapper;
import ru.magnit.magreportbackend.mapper.folderreport.FolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.folderreport.FolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.olap.OlapConfigurationBackupMapper;
import ru.magnit.magreportbackend.mapper.olap.ReportOlapConfigurationBackupMapper;
import ru.magnit.magreportbackend.mapper.report.FavReportBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportExcelTemplateBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.reportjob.ReportJobBackupMapper;
import ru.magnit.magreportbackend.mapper.reportjob.ReportJobFilterBackupMapper;
import ru.magnit.magreportbackend.mapper.reportjob.ReportJobStatisticsBackupMapper;
import ru.magnit.magreportbackend.mapper.reportjob.ReportJobTupleBackupMapper;
import ru.magnit.magreportbackend.mapper.reportjob.ReportJobTupleFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.reportjob.ReportJobUserBackupMapper;
import ru.magnit.magreportbackend.mapper.role.RoleBackupMapper;
import ru.magnit.magreportbackend.mapper.role.RoleDomainGroupBackupMapper;
import ru.magnit.magreportbackend.mapper.role.RoleTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.DestinationEmailBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.DestinationRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.DestinationTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.DestinationUserBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.ScheduleBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.ScheduleCalendarInfoBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.ScheduleTaskBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.ScheduleTaskStatusBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.ScheduleTaskTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.schedule.ScheduleTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterDatasetBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterDatasetFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterFolderBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterFolderRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterFolderRolePermissionBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterRoleBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterTupleBackupMapper;
import ru.magnit.magreportbackend.mapper.securityfilter.SecurityFilterTupleValueBackupMapper;
import ru.magnit.magreportbackend.mapper.serversettings.JobTokenBackupMapper;
import ru.magnit.magreportbackend.mapper.serversettings.ServerMailTemplateBackupMapper;
import ru.magnit.magreportbackend.mapper.serversettings.ServerSettingsBackupMapper;
import ru.magnit.magreportbackend.mapper.serversettings.ServerSettingsJournalBackupMapper;
import ru.magnit.magreportbackend.mapper.theme.ThemeBackupMapper;
import ru.magnit.magreportbackend.repository.DataSetFieldRepository;
import ru.magnit.magreportbackend.repository.DataSetFolderRepository;
import ru.magnit.magreportbackend.repository.DataSetFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.DataSetFolderRoleRepository;
import ru.magnit.magreportbackend.repository.DataSetRepository;
import ru.magnit.magreportbackend.repository.DataSourceFolderRepository;
import ru.magnit.magreportbackend.repository.DataSourceFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.DataSourceFolderRoleRepository;
import ru.magnit.magreportbackend.repository.DataSourceRepository;
import ru.magnit.magreportbackend.repository.DataSourceTypeRepository;
import ru.magnit.magreportbackend.repository.DestinationEmailRepository;
import ru.magnit.magreportbackend.repository.DestinationRoleRepository;
import ru.magnit.magreportbackend.repository.DestinationTypeRepository;
import ru.magnit.magreportbackend.repository.DestinationUserRepository;
import ru.magnit.magreportbackend.repository.DomainGroupRepository;
import ru.magnit.magreportbackend.repository.ExcelTemplateFolderRepository;
import ru.magnit.magreportbackend.repository.ExcelTemplateFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.ExcelTemplateFolderRoleRepository;
import ru.magnit.magreportbackend.repository.ExcelTemplateRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthSecurityFilterRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthSourceFieldFiFieldRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthSourceFieldRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthSourceFieldTypeRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthSourceRepository;
import ru.magnit.magreportbackend.repository.ExternalAuthSourceTypeRepository;
import ru.magnit.magreportbackend.repository.FavReportRepository;
import ru.magnit.magreportbackend.repository.FilterInstanceFieldRepository;
import ru.magnit.magreportbackend.repository.FilterInstanceFolderRepository;
import ru.magnit.magreportbackend.repository.FilterInstanceFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.FilterInstanceFolderRoleRepository;
import ru.magnit.magreportbackend.repository.FilterInstanceRepository;
import ru.magnit.magreportbackend.repository.FilterReportFieldRepository;
import ru.magnit.magreportbackend.repository.FilterReportGroupRepository;
import ru.magnit.magreportbackend.repository.FilterReportRepository;
import ru.magnit.magreportbackend.repository.FilterTemplateFieldRepository;
import ru.magnit.magreportbackend.repository.FilterTemplateFolderRepository;
import ru.magnit.magreportbackend.repository.FilterTemplateFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.FilterTemplateFolderRoleRepository;
import ru.magnit.magreportbackend.repository.FilterTemplateRepository;
import ru.magnit.magreportbackend.repository.FilterTypeRepository;
import ru.magnit.magreportbackend.repository.FolderReportRepository;
import ru.magnit.magreportbackend.repository.FolderRepository;
import ru.magnit.magreportbackend.repository.FolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.FolderRoleRepository;
import ru.magnit.magreportbackend.repository.JobTokenRepository;
import ru.magnit.magreportbackend.repository.OlapConfigurationRepository;
import ru.magnit.magreportbackend.repository.ReportExcelTemplateRepository;
import ru.magnit.magreportbackend.repository.ReportFieldRepository;
import ru.magnit.magreportbackend.repository.ReportFolderRepository;
import ru.magnit.magreportbackend.repository.ReportFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.ReportFolderRoleRepository;
import ru.magnit.magreportbackend.repository.ReportJobFilterRepository;
import ru.magnit.magreportbackend.repository.ReportJobRepository;
import ru.magnit.magreportbackend.repository.ReportJobStatisticsRepository;
import ru.magnit.magreportbackend.repository.ReportJobTupleFieldRepository;
import ru.magnit.magreportbackend.repository.ReportJobTupleRepository;
import ru.magnit.magreportbackend.repository.ReportJobUserRepository;
import ru.magnit.magreportbackend.repository.ReportOlapConfigurationRepository;
import ru.magnit.magreportbackend.repository.ReportRepository;
import ru.magnit.magreportbackend.repository.RoleDomainGroupRepository;
import ru.magnit.magreportbackend.repository.RoleRepository;
import ru.magnit.magreportbackend.repository.RoleTypeRepository;
import ru.magnit.magreportbackend.repository.ScheduleCalendarInfoRepository;
import ru.magnit.magreportbackend.repository.ScheduleRepository;
import ru.magnit.magreportbackend.repository.ScheduleTaskRepository;
import ru.magnit.magreportbackend.repository.ScheduleTaskStatusRepository;
import ru.magnit.magreportbackend.repository.ScheduleTaskTypeRepository;
import ru.magnit.magreportbackend.repository.ScheduleTypeRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterDataSetFieldRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterDataSetRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterFolderRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterFolderRolePermissionRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterFolderRoleRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterRoleRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterTupleRepository;
import ru.magnit.magreportbackend.repository.SecurityFilterTupleValueRepository;
import ru.magnit.magreportbackend.repository.ServerMailTemplateRepository;
import ru.magnit.magreportbackend.repository.ServerMailTemplateTypeRepository;
import ru.magnit.magreportbackend.repository.ServerSettingsFolderRepository;
import ru.magnit.magreportbackend.repository.ServerSettingsJournalRepository;
import ru.magnit.magreportbackend.repository.ServerSettingsRepository;
import ru.magnit.magreportbackend.repository.ThemeRepository;
import ru.magnit.magreportbackend.repository.ThemeTypeRepository;
import ru.magnit.magreportbackend.repository.UserReportExcelTemplateRepository;
import ru.magnit.magreportbackend.repository.UserRepository;
import ru.magnit.magreportbackend.repository.UserRoleRepository;
import ru.magnit.magreportbackend.repository.UserRoleTypeRepository;
import ru.magnit.magreportbackend.repository.UserStatusRepository;
import ru.magnit.magreportbackend.util.JsonUtils;

import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private File file ;

    private final DataSetRepository dataSetRepository;
    private final DataSetFieldRepository dataSetFieldRepository;
    private final DataSetFolderRepository dataSetFolderRepository;
    private final DataSetFolderRoleRepository dataSetFolderRoleRepository;
    private final DataSetFolderRolePermissionRepository dataSetFolderRolePermissionRepository;
    private final DataSourceRepository dataSourceRepository;
    private final DataSourceFolderRepository dataSourceFolderRepository;
    private final DataSourceFolderRoleRepository dataSourceFolderRoleRepository;
    private final DataSourceFolderRolePermissionRepository dataSourceFolderRolePermissionRepository;
    private final DestinationEmailRepository destinationEmailRepository;
    private final DestinationRoleRepository destinationRoleRepository;
    private final DestinationTypeRepository destinationTypeRepository;
    private final DestinationUserRepository destinationUserRepository;
    private final DomainGroupRepository domainGroupRepository;
    private final ExcelTemplateRepository excelTemplateRepository;
    private final ExcelTemplateFolderRepository excelTemplateFolderRepository;
    private final ExcelTemplateFolderRoleRepository excelTemplateFolderRoleRepository;
    private final ExcelTemplateFolderRolePermissionRepository excelTemplateFolderRolePermissionRepository;
    private final ExternalAuthRepository externalAuthRepository;
    private final ExternalAuthSecurityFilterRepository externalAuthSecurityFilterRepository;
    private final ExternalAuthSourceRepository externalAuthSourceRepository;
    private final ExternalAuthSourceFieldRepository externalAuthSourceFieldRepository;
    private final ExternalAuthSourceFieldFiFieldRepository externalAuthSourceFieldFiFieldRepository;
    private final ExternalAuthSourceFieldTypeRepository externalAuthSourceFieldTypeRepository;
    private final ExternalAuthSourceTypeRepository externalAuthSourceTypeRepository;
    private final FavReportRepository favReportRepository;
    private final FilterInstanceRepository filterInstanceRepository;
    private final FilterInstanceFieldRepository filterInstanceFieldRepository;
    private final FilterInstanceFolderRepository filterInstanceFolderRepository;
    private final FilterInstanceFolderRoleRepository filterInstanceFolderRoleRepository;
    private final FilterInstanceFolderRolePermissionRepository filterInstanceFolderRolePermissionRepository;
    private final FilterReportRepository filterReportRepository;
    private final FilterReportFieldRepository filterReportFieldRepository;
    private final FilterReportGroupRepository filterReportGroupRepository;
    private final FilterTemplateRepository filterTemplateRepository;
    private final FilterTemplateFieldRepository filterTemplateFieldRepository;
    private final FilterTemplateFolderRepository filterTemplateFolderRepository;
    private final FilterTemplateFolderRoleRepository filterTemplateFolderRoleRepository;
    private final FilterTemplateFolderRolePermissionRepository filterTemplateFolderRolePermissionRepository;
    private final FilterTypeRepository filterTypeRepository;
    private final FolderRepository folderRepository;
    private final FolderReportRepository folderReportRepository;
    private final FolderRoleRepository folderRoleRepository;
    private final FolderRolePermissionRepository folderRolePermissionRepository;
    private final JobTokenRepository jobTokenRepository;
    private final OlapConfigurationRepository olapConfigurationRepository;
    private final ReportRepository reportRepository;
    private final ReportExcelTemplateRepository reportExcelTemplateRepository;
    private final ReportFieldRepository reportFieldRepository;
    private final ReportFolderRepository reportFolderRepository;
    private final ReportFolderRoleRepository reportFolderRoleRepository;
    private final ReportFolderRolePermissionRepository reportFolderRolePermissionRepository;
    private final ReportJobRepository reportJobRepository;
    private final ReportJobFilterRepository reportJobFilterRepository;
    private final ReportJobStatisticsRepository reportJobStatisticsRepository;
    private final ReportJobTupleRepository reportJobTupleRepository;
    private final ReportJobTupleFieldRepository reportJobTupleFieldRepository;
    private final ReportJobUserRepository reportJobUserRepository;
    private final ReportOlapConfigurationRepository reportOlapConfigurationRepository;
    private final RoleRepository roleRepository;
    private final RoleDomainGroupRepository roleDomainGroupRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleTaskRepository scheduleTaskRepository;
    private final SecurityFilterRepository securityFilterRepository;
    private final SecurityFilterDataSetRepository securityFilterDataSetRepository;
    private final SecurityFilterDataSetFieldRepository securityFilterDataSetFieldRepository;
    private final SecurityFilterFolderRepository securityFilterFolderRepository;
    private final SecurityFilterFolderRoleRepository securityFilterFolderRoleRepository;
    private final SecurityFilterFolderRolePermissionRepository securityFilterFolderRolePermissionRepository;
    private final SecurityFilterRoleRepository securityFilterRoleRepository;
    private final SecurityFilterTupleRepository securityFilterTupleRepository;
    private final SecurityFilterTupleValueRepository securityFilterTupleValueRepository;
    private final ServerMailTemplateRepository serverMailTemplateRepository;
    private final ServerSettingsRepository serverSettingsRepository;
    private final ServerSettingsJournalRepository serverSettingsJournalRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final UserReportExcelTemplateRepository userReportExcelTemplateRepository;
    private final UserRoleRepository userRoleRepository;

    private final DataSetBackupMapper dataSetBackupMapper;
    private final DataSetFieldBackupMapper dataSetFieldBackupMapper;
    private final DataSetFolderBackupMapper dataSetFolderBackupMapper;
    private final DataSetFolderRoleBackupMapper dataSetFolderRoleBackupMapper;
    private final DataSetFolderRolePermissionBackupMapper dataSetFolderRolePermissionBackupMapper;
    private final DataSourceBackupMapper dataSourceBackupMapper;
    private final DataSourceFolderBackupMapper dataSourceFolderBackupMapper;
    private final DataSourceFolderRoleBackupMapper dataSourceFolderRoleBackupMapper;
    private final DataSourceFolderRolePermissionBackupMapper dataSourceFolderRolePermissionBackupMapper;
    private final DestinationEmailBackupMapper destinationEmailBackupMapper;
    private final DestinationRoleBackupMapper destinationRoleBackupMapper;
    private final DestinationTypeBackupMapper destinationTypeBackupMapper;
    private final DestinationUserBackupMapper destinationUserBackupMapper;
    private final DomainGroupBackupMapper domainGroupBackupMapper;
    private final ExcelTemplateBackupMapper excelTemplateBackupMapper;
    private final ExcelTemplateFolderBackupMapper excelTemplateFolderBackupMapper;
    private final ExcelTemplateFolderRoleBackupMapper excelTemplateFolderRoleBackupMapper;
    private final ExcelTemplateFolderRolePermissionBackupMapper excelTemplateFolderRolePermissionBackupMapper;
    private final ExternalAuthBackupMapper externalAuthBackupMapper;
    private final ExternalAuthSecurityFilterBackupMapper externalAuthSecurityFilterBackupMapper;
    private final ExternalAuthSourceBackupMapper externalAuthSourceBackupMapper;
    private final ExternalAuthSourceFieldBackupMapper externalAuthSourceFieldBackupMapper;
    private final ExternalAuthSourceFieldFiFieldBackupMapper externalAuthSourceFieldFiFieldBackupMapper;
    private final ExternalAuthSourceFieldTypeBackupMapper externalAuthSourceFieldTypeBackupMapper;
    private final ExternalAuthSourceTypeBackupMapper externalAuthSourceTypeBackupMapper;
    private final FavReportBackupMapper favReportBackupMapper;
    private final FilterInstanceBackupMapper filterInstanceBackupMapper;
    private final FilterInstanceFieldBackupMapper filterInstanceFieldBackupMapper;
    private final FilterInstanceFolderBackupMapper filterInstanceFolderBackupMapper;
    private final FilterInstanceFolderRoleBackupMapper filterInstanceFolderRoleBackupMapper;
    private final FilterInstanceFolderRolePermissionBackupMapper filterInstanceFolderRolePermissionBackupMapper;
    private final FilterReportBackupMapper filterReportBackupMapper;
    private final FilterReportFieldBackupMapper filterReportFieldBackupMapper;
    private final FilterReportGroupBackupMapper filterReportGroupBackupMapper;
    private final FilterTemplateBackupMapper filterTemplateBackupMapper;
    private final FilterTemplateFieldBackupMapper filterTemplateFieldBackupMapper;
    private final FilterTemplateFolderBackupMapper filterTemplateFolderBackupMapper;
    private final FilterTemplateFolderRoleBackupMapper filterTemplateFolderRoleBackupMapper;
    private final FilterTemplateFolderRolePermissionBackupMapper filterTemplateFolderRolePermissionBackupMapper;
    private final FilterTypeBackupMapper filterTypeBackupMapper;
    private final FolderBackupMapper folderBackupMapper;
    private final FolderReportBackupMapper folderReportBackupMapper;
    private final FolderRoleBackupMapper folderRoleBackupMapper;
    private final FolderRolePermissionBackupMapper folderRolePermissionBackupMapper;
    private final JobTokenBackupMapper jobTokenBackupMapper;
    private final OlapConfigurationBackupMapper olapConfigurationBackupMapper;
    private final ReportBackupMapper reportBackupMapper;
    private final ReportExcelTemplateBackupMapper reportExcelTemplateBackupMapper;
    private final ReportFieldBackupMapper reportFieldBackupMapper;
    private final ReportFolderBackupMapper reportFolderBackupMapper;
    private final ReportFolderRoleBackupMapper reportFolderRoleBackupMapper;
    private final ReportFolderRolePermissionBackupMapper reportFolderRolePermissionBackupMapper;
    private final ReportJobBackupMapper reportJobBackupMapper;
    private final ReportJobFilterBackupMapper reportJobFilterBackupMapper;
    private final ReportJobStatisticsBackupMapper reportJobStatisticsBackupMapper;
    private final ReportJobTupleBackupMapper reportJobTupleBackupMapper;
    private final ReportJobTupleFieldBackupMapper reportJobTupleFieldBackupMapper;
    private final ReportJobUserBackupMapper reportJobUserBackupMapper;
    private final ReportOlapConfigurationBackupMapper reportOlapConfigurationBackupMapper;
    private final RoleBackupMapper roleBackupMapper;
    private final RoleDomainGroupBackupMapper roleDomainGroupBackupMapper;
    private final ScheduleBackupMapper scheduleBackupMapper;
    private final ScheduleTaskBackupMapper scheduleTaskBackupMapper;
    private final SecurityFilterBackupMapper securityFilterBackupMapper;
    private final SecurityFilterDatasetBackupMapper securityFilterDatasetBackupMapper;
    private final SecurityFilterDatasetFieldBackupMapper securityFilterDatasetFieldBackupMapper;
    private final SecurityFilterFolderBackupMapper securityFilterFolderBackupMapper;
    private final SecurityFilterFolderRoleBackupMapper securityFilterFolderRoleBackupMapper;
    private final SecurityFilterFolderRolePermissionBackupMapper securityFilterFolderRolePermissionBackupMapper;
    private final SecurityFilterRoleBackupMapper securityFilterRoleBackupMapper;
    private final SecurityFilterTupleBackupMapper securityFilterTupleBackupMapper;
    private final SecurityFilterTupleValueBackupMapper securityFilterTupleValueBackupMapper;
    private final ServerMailTemplateBackupMapper serverMailTemplateBackupMapper;
    private final ServerSettingsBackupMapper serverSettingsBackupMapper;
    private final ServerSettingsJournalBackupMapper serverSettingsJournalBackupMapper;
    private final ThemeBackupMapper themeBackupMapper;
    private final UsersBackupMapper usersBackupMapper;
    private final UserReportExcelTemplateBackupMapper userReportExcelTemplateBackupMapper;
    private final UserRoleBackupMapper userRoleBackupMapper;


    @Transactional
    public byte[] createBackup(BackupRequest request) {

        try {
            file = new File("backup.txt");
            var output = new BufferedWriter(new FileWriter(file));

            output.write("{");
            output.newLine();

            //ASM
            writeRow("ExternalAuth", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthBackupMapper.from(externalAuthRepository.findAll()))), output , false);
            writeRow("ExternalAuthSecurityFilter", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthSecurityFilterBackupMapper.from(externalAuthSecurityFilterRepository.findAll()))), output , false);
            writeRow("ExternalAuthSource", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthSourceBackupMapper.from(externalAuthSourceRepository.findAll()))), output , false);
            writeRow("ExternalAuthSourceField", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthSourceFieldBackupMapper.from(externalAuthSourceFieldRepository.findAll()))), output , false);
            writeRow("ExternalAuthSourceFieldFIField", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthSourceFieldFiFieldBackupMapper.from(externalAuthSourceFieldFiFieldRepository.findAll()))), output , false);
            writeRow("ExternalAuthSourceFieldType", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthSourceFieldTypeBackupMapper.from(externalAuthSourceFieldTypeRepository.findAll()))), output , false);
            writeRow("ExternalAuthSourceType", JsonUtils.getJsonFromObjects(new ArrayList<>(externalAuthSourceTypeBackupMapper.from(externalAuthSourceTypeRepository.findAll()))), output , false);
           
            //DataSet
            writeRow("DataSet", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSetBackupMapper.from(dataSetRepository.findAll()))), output , false);
            writeRow("DataSetField", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSetFieldBackupMapper.from(dataSetFieldRepository.findAll()))), output , false);
            writeRow("DataSetFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSetFolderBackupMapper.from(dataSetFolderRepository.findAll()))), output , false);
            writeRow("DataSetFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSetFolderRoleBackupMapper.from(dataSetFolderRoleRepository.findAll()))), output , false);
            writeRow("DataSetFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSetFolderRolePermissionBackupMapper.from(dataSetFolderRolePermissionRepository.findAll()))), output , false);

            //DataSource
            writeRow("DataSource", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSourceBackupMapper.from(dataSourceRepository.findAll()))), output , false);
            writeRow("DataSourceFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSourceFolderBackupMapper.from(dataSourceFolderRepository.findAll()))), output , false);
            writeRow("DataSourceFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSourceFolderRoleBackupMapper.from(dataSourceFolderRoleRepository.findAll()))), output , false);
            writeRow("DataSourceFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(dataSourceFolderRolePermissionBackupMapper.from(dataSourceFolderRolePermissionRepository.findAll()))), output , false);

            //ExcelTemplate
            writeRow("ExcelTemplate", JsonUtils.getJsonFromObjects(new ArrayList<>(excelTemplateBackupMapper.from(excelTemplateRepository.findAll()))), output , false);
            writeRow("ExcelTemplateFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(excelTemplateFolderBackupMapper.from(excelTemplateFolderRepository.findAll()))), output , false);
            writeRow("ExcelTemplateFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(excelTemplateFolderRoleBackupMapper.from(excelTemplateFolderRoleRepository.findAll()))), output , false);
            writeRow("ExcelTemplateFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(excelTemplateFolderRolePermissionBackupMapper.from(excelTemplateFolderRolePermissionRepository.findAll()))), output , false);

            //FilterInstance
            writeRow("FilterInstance", JsonUtils.getJsonFromObjects(new ArrayList<>(filterInstanceBackupMapper.from(filterInstanceRepository.findAll()))), output , false);
            writeRow("FilterInstanceField", JsonUtils.getJsonFromObjects(new ArrayList<>(filterInstanceFieldBackupMapper.from(filterInstanceFieldRepository.findAll()))), output , false);
            writeRow("FilterInstanceFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(filterInstanceFolderBackupMapper.from(filterInstanceFolderRepository.findAll()))), output , false);
            writeRow("FilterInstanceFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(filterInstanceFolderRoleBackupMapper.from(filterInstanceFolderRoleRepository.findAll()))),output , false);
            writeRow("FilterInstanceFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(filterInstanceFolderRolePermissionBackupMapper.from(filterInstanceFolderRolePermissionRepository.findAll()))), output , false);

            //FilterReport
            writeRow("FilterReport", JsonUtils.getJsonFromObjects(new ArrayList<>(filterReportBackupMapper.from(filterReportRepository.findAll()))), output , false);
            writeRow("FilterReportField", JsonUtils.getJsonFromObjects(new ArrayList<>(filterReportFieldBackupMapper.from(filterReportFieldRepository.findAll()))), output , false);
            writeRow("FilterReportGroup", JsonUtils.getJsonFromObjects(new ArrayList<>(filterReportGroupBackupMapper.from(filterReportGroupRepository.findAll()))), output , false);

            //FilterTemplate
            writeRow("FilterTemplate", JsonUtils.getJsonFromObjects(new ArrayList<>(filterTemplateBackupMapper.from(filterTemplateRepository.findAll()))), output , false);
            writeRow("FilterTemplateField", JsonUtils.getJsonFromObjects(new ArrayList<>(filterTemplateFieldBackupMapper.from(filterTemplateFieldRepository.findAll()))), output , false);
            writeRow("FilterTemplateFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(filterTemplateFolderBackupMapper.from(filterTemplateFolderRepository.findAll()))), output , false);
            writeRow("FilterTemplateFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(filterTemplateFolderRoleBackupMapper.from(filterTemplateFolderRoleRepository.findAll()))), output , false);
            writeRow("FilterTemplateFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(filterTemplateFolderRolePermissionBackupMapper.from(filterTemplateFolderRolePermissionRepository.findAll()))), output , false);
            writeRow("FilterType", JsonUtils.getJsonFromObjects(new ArrayList<>(filterTypeBackupMapper.from(filterTypeRepository.findAll()))), output , false);

            //Folder
            writeRow("Folder", JsonUtils.getJsonFromObjects(new ArrayList<>(folderBackupMapper.from(folderRepository.findAll()))), output , false);
            writeRow("FolderReport", JsonUtils.getJsonFromObjects(new ArrayList<>(folderReportBackupMapper.from(folderReportRepository.findAll()))), output , false);
            writeRow("FolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(folderRoleBackupMapper.from(folderRoleRepository.findAll()))), output , false);
            writeRow("FolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(folderRolePermissionBackupMapper.from(folderRolePermissionRepository.findAll()))), output , false);

            //OlapConfiguration
            writeRow("OlapConfiguration", JsonUtils.getJsonFromObjects(new ArrayList<>(olapConfigurationBackupMapper.from(olapConfigurationRepository.findAll()))), output , false);
            writeRow("ReportOlapConfiguration", JsonUtils.getJsonFromObjects(new ArrayList<>(reportOlapConfigurationBackupMapper.from(reportOlapConfigurationRepository.findAll()))), output , false);

            //Report
            writeRow("FavReport", JsonUtils.getJsonFromObjects(new ArrayList<>(favReportBackupMapper.from(favReportRepository.findAll()))), output , false);
            writeRow("Report", JsonUtils.getJsonFromObjects(new ArrayList<>(reportBackupMapper.from(reportRepository.findAll()))), output , false);
            writeRow("ReportExcelTemplate", JsonUtils.getJsonFromObjects(new ArrayList<>(reportExcelTemplateBackupMapper.from(reportExcelTemplateRepository.findAll()))), output , false);
            writeRow("ReportField", JsonUtils.getJsonFromObjects(new ArrayList<>(reportFieldBackupMapper.from(reportFieldRepository.findAll()))), output , false);
            writeRow("ReportFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(reportFolderBackupMapper.from(reportFolderRepository.findAll()))), output , false);
            writeRow("ReportFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(reportFolderRoleBackupMapper.from(reportFolderRoleRepository.findAll()))), output , false);
            writeRow("ReportFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(reportFolderRolePermissionBackupMapper.from(reportFolderRolePermissionRepository.findAll()))), output , false);

            //ReportJob
            if (request.isJobs()) {
                writeRow("ReportJob", JsonUtils.getJsonFromObjects(new ArrayList<>(reportJobBackupMapper.from(reportJobRepository.findAll()))), output , false);
                writeRow("ReportJobFilter", JsonUtils.getJsonFromObjects(new ArrayList<>(reportJobFilterBackupMapper.from(reportJobFilterRepository.findAll()))), output , false);
                writeRow("ReportJobTuple", JsonUtils.getJsonFromObjects(new ArrayList<>(reportJobTupleBackupMapper.from(reportJobTupleRepository.findAll()))), output , false);
                writeRow("ReportJobTupleField", JsonUtils.getJsonFromObjects(new ArrayList<>(reportJobTupleFieldBackupMapper.from(reportJobTupleFieldRepository.findAll()))), output , false);
                writeRow("ReportJobUser", JsonUtils.getJsonFromObjects(new ArrayList<>(reportJobUserBackupMapper.from(reportJobUserRepository.findAll()))), output , false);
            }

            if (request.isStatistics())
                writeRow("ReportJobStatistics", JsonUtils.getJsonFromObjects(new ArrayList<>(reportJobStatisticsBackupMapper.from(reportJobStatisticsRepository.findAll()))), output , false);

            //Role
            writeRow("Role", JsonUtils.getJsonFromObjects(new ArrayList<>(roleBackupMapper.from(roleRepository.findAll()))), output , false);
            writeRow("RoleDomainGroup", JsonUtils.getJsonFromObjects(new ArrayList<>(roleDomainGroupBackupMapper.from(roleDomainGroupRepository.findAll()))), output , false);

            //Schedule
            writeRow("DestinationEmail", JsonUtils.getJsonFromObjects(new ArrayList<>(destinationEmailBackupMapper.from(destinationEmailRepository.findAll()))), output , false);
            writeRow("DestinationRole", JsonUtils.getJsonFromObjects(new ArrayList<>(destinationRoleBackupMapper.from(destinationRoleRepository.findAll()))), output , false);
            writeRow("DestinationType", JsonUtils.getJsonFromObjects(new ArrayList<>(destinationTypeBackupMapper.from(destinationTypeRepository.findAll()))), output , false);
            writeRow("DestinationUser", JsonUtils.getJsonFromObjects(new ArrayList<>(destinationUserBackupMapper.from(destinationUserRepository.findAll()))), output , false);
            writeRow("Schedule", JsonUtils.getJsonFromObjects(new ArrayList<>(scheduleBackupMapper.from(scheduleRepository.findAll()))), output , false);
            writeRow("ScheduleScheduleTask", JsonUtils.getJsonFromObjects(new ArrayList<>(scheduleRepository.getAllForBackup())), output , false);
            writeRow("ScheduleTask", JsonUtils.getJsonFromObjects(new ArrayList<>(scheduleTaskBackupMapper.from(scheduleTaskRepository.findAll()))), output , false);

            //Security Filter
            writeRow("SecurityFilter", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterBackupMapper.from(securityFilterRepository.findAll()))), output , false);
            writeRow("SecurityFilterDataset", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterDatasetBackupMapper.from(securityFilterDataSetRepository.findAll()))), output , false);
            writeRow("SecurityFilterDatasetField", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterDatasetFieldBackupMapper.from(securityFilterDataSetFieldRepository.findAll()))), output , false);
            writeRow("SecurityFilterFolder", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterFolderBackupMapper.from(securityFilterFolderRepository.findAll()))), output , false);
            writeRow("SecurityFilterFolderRole", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterFolderRoleBackupMapper.from(securityFilterFolderRoleRepository.findAll()))), output , false);
            writeRow("SecurityFilterFolderRolePermission", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterFolderRolePermissionBackupMapper.from(securityFilterFolderRolePermissionRepository.findAll()))), output , false);
            writeRow("SecurityFilterRole", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterRoleBackupMapper.from(securityFilterRoleRepository.findAll()))), output , false);
            writeRow("SecurityFilterTuple", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterTupleBackupMapper.from(securityFilterTupleRepository.findAll()))), output , false);
            writeRow("SecurityFilterTupleValue", JsonUtils.getJsonFromObjects(new ArrayList<>(securityFilterTupleValueBackupMapper.from(securityFilterTupleValueRepository.findAll()))), output , false);

            //ServerSettings
            writeRow("JobToken", JsonUtils.getJsonFromObjects(new ArrayList<>(jobTokenBackupMapper.from(jobTokenRepository.findAll()))), output , false);
            writeRow("ServerMailTemplate", JsonUtils.getJsonFromObjects(new ArrayList<>(serverMailTemplateBackupMapper.from(serverMailTemplateRepository.findAll()))), output , false);
            writeRow("ServerSettings", JsonUtils.getJsonFromObjects(new ArrayList<>(serverSettingsBackupMapper.from(serverSettingsRepository.findAll()))),output , false);
            writeRow("ServerSettingsJournal", JsonUtils.getJsonFromObjects(new ArrayList<>(serverSettingsJournalBackupMapper.from(serverSettingsJournalRepository.findAll()))),output , false);
            writeRow("ThemeBackupMapper", JsonUtils.getJsonFromObjects(new ArrayList<>(themeBackupMapper.from(themeRepository.findAll()))), output , false);

            //User
            writeRow("DomainGroup", JsonUtils.getJsonFromObjects(new ArrayList<>(domainGroupBackupMapper.from(domainGroupRepository.findAll()))), output , false);
            writeRow("UsersBackupMapper", JsonUtils.getJsonFromObjects(new ArrayList<>(usersBackupMapper.from(userRepository.findAll()))), output , false);
            writeRow("UserReportExcelTemplateBackupMapper", JsonUtils.getJsonFromObjects(new ArrayList<>(userReportExcelTemplateBackupMapper.from(userReportExcelTemplateRepository.findAll()))), output , false);
            writeRow("UserRoleBackupMapper", JsonUtils.getJsonFromObjects(new ArrayList<>(userRoleBackupMapper.from(userRoleRepository.findAll()))), output , true);

            output.write("}");
            output.flush();
            output.close();

            InputStream in = Files.newInputStream(file.toPath());
            return IOUtils.toByteArray(in);

        } catch (IOException ex) {
            log.error("Error trying to get backup file", ex);
            throw new FileSystemException("Error trying to get backup file", ex);
        }


    }

    private void writeRow(String nameObject, List<String> objects, BufferedWriter writer, boolean endRow) throws IOException {
        writer.write("\"" + nameObject + "\":" + objects + (endRow ? "" : ","));
        writer.newLine();
    }

}
