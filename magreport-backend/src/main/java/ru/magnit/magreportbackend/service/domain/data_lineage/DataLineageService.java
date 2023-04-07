package ru.magnit.magreportbackend.service.domain.data_lineage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataSetFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.dataset.DataTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceBackupTuple;
import ru.magnit.magreportbackend.dto.backup.datasource.DataSourceTypeBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderBackupTuple;
import ru.magnit.magreportbackend.dto.backup.folder.FolderReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFieldBackupTuple;
import ru.magnit.magreportbackend.dto.backup.report.ReportFolderBackupTuple;
import ru.magnit.magreportbackend.dto.response.dataset.DataSetTypeResponse;
import ru.magnit.magreportbackend.dto.response.report.PivotFieldTypeResponse;
import ru.magnit.magreportbackend.mapper.dataset.DataSetBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataSetTypeResponseMapper;
import ru.magnit.magreportbackend.mapper.dataset.DataTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceBackupMapper;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceTypeBackupMapper;
import ru.magnit.magreportbackend.mapper.folderreport.FolderBackupMapper;
import ru.magnit.magreportbackend.mapper.folderreport.FolderReportBackupMapper;
import ru.magnit.magreportbackend.mapper.report.PivotFieldTypeResponseMapper;
import ru.magnit.magreportbackend.mapper.report.ReportBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportFieldBackupMapper;
import ru.magnit.magreportbackend.mapper.report.ReportFolderBackupMapper;
import ru.magnit.magreportbackend.repository.DataSetDataTypeRepository;
import ru.magnit.magreportbackend.repository.DataSetFieldRepository;
import ru.magnit.magreportbackend.repository.DataSetRepository;
import ru.magnit.magreportbackend.repository.DataSetTypeRepository;
import ru.magnit.magreportbackend.repository.DataSourceRepository;
import ru.magnit.magreportbackend.repository.DataSourceTypeRepository;
import ru.magnit.magreportbackend.repository.FolderReportRepository;
import ru.magnit.magreportbackend.repository.FolderRepository;
import ru.magnit.magreportbackend.repository.PivotFieldTypeRepository;
import ru.magnit.magreportbackend.repository.ReportFieldRepository;
import ru.magnit.magreportbackend.repository.ReportFolderRepository;
import ru.magnit.magreportbackend.repository.ReportRepository;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLineageService {
    private final DataSetDataTypeRepository dataTypeRepository;
    private final PivotFieldTypeRepository pivotFieldTypeRepository;
    private final DataSourceTypeRepository dataSourceTypeRepository;
    private final DataSetTypeRepository dataSetTypeRepository;
    private final DataSourceRepository dataSourceRepository;
    private final DataSetRepository dataSetRepository;
    private final DataSetFieldRepository dataSetFieldRepository;
    private final ReportRepository reportRepository;
    private final ReportFieldRepository reportFieldRepository;
    private final ReportFolderRepository reportFolderRepository;
    private final FolderReportRepository folderReportRepository;
    private final FolderRepository folderRepository;

    private final DataTypeBackupMapper dataTypeBackupMapper;
    private final PivotFieldTypeResponseMapper pivotFieldTypeResponseMapper;
    private final DataSourceTypeBackupMapper dataSourceTypeBackupMapper;
    private final DataSetTypeResponseMapper dataSetTypeResponseMapper;
    private final DataSourceBackupMapper dataSourceBackupMapper;
    private final DataSetBackupMapper dataSetBackupMapper;
    private final DataSetFieldBackupMapper dataSetFieldBackupMapper;
    private final ReportBackupMapper reportBackupMapper;
    private final ReportFieldBackupMapper reportFieldBackupMapper;
    private final ReportFolderBackupMapper reportFolderBackupMapper;
    private final FolderReportBackupMapper folderReportBackupMapper;
    private final FolderBackupMapper folderBackupMapper;

    @Transactional
    public List<DataTypeBackupTuple> getAllDataTypes() {
        return dataTypeBackupMapper.from(dataTypeRepository.findAll());
    }

    @Transactional
    public List<PivotFieldTypeResponse> getAllPivotTypes() {
        return pivotFieldTypeResponseMapper.from(pivotFieldTypeRepository.findAll());
    }

    @Transactional
    public List<DataSourceTypeBackupTuple> getAllDataSourceTypes() {
        return dataSourceTypeBackupMapper.from(dataSourceTypeRepository.findAll());
    }

    @Transactional
    public List<DataSetTypeResponse> getAllDatasetTypes() {
        return dataSetTypeResponseMapper.from(dataSetTypeRepository.findAll());
    }

    @Transactional
    public List<DataSourceBackupTuple> getAllDataSources() {
        return dataSourceBackupMapper.from(dataSourceRepository.findAll());
    }

    @Transactional
    public List<DataSetBackupTuple> getAllDataSets() {
        return dataSetBackupMapper.from(dataSetRepository.findAll());
    }

    @Transactional
    public List<DataSetFieldBackupTuple> getAllDataSetFields() {
        return dataSetFieldBackupMapper.from(dataSetFieldRepository.findAll());
    }

    @Transactional
    public List<ReportBackupTuple> getAllReports() {
        return reportBackupMapper.from(reportRepository.findAll());
    }

    @Transactional
    public List<ReportFieldBackupTuple> getAllReportFields() {
        return reportFieldBackupMapper.from(reportFieldRepository.findAll());
    }

    @Transactional
    public List<ReportFolderBackupTuple> getAllDevReportFolders() {
        return reportFolderBackupMapper.from(reportFolderRepository.findAll());
    }

    @Transactional
    public List<FolderReportBackupTuple> getAllUserReportFolderLinks() {
        return folderReportBackupMapper.from(folderReportRepository.findAll());
    }

    @Transactional
    public List<FolderBackupTuple> getAllUserReportFolders() {
        return folderBackupMapper.from(folderRepository.findAll());
    }
}
