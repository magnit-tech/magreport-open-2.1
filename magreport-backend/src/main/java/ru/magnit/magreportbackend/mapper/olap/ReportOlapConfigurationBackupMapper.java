package ru.magnit.magreportbackend.mapper.olap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.olap.ReportOlapConfiguration;
import ru.magnit.magreportbackend.dto.backup.olap.ReportOlapConfigurationBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportOlapConfigurationBackupMapper implements Mapper<ReportOlapConfigurationBackupTuple, ReportOlapConfiguration> {
    @Override
    public ReportOlapConfigurationBackupTuple from(ReportOlapConfiguration source) {
        return new ReportOlapConfigurationBackupTuple(
                source.getId(),
                source.getReport() == null ? null : source.getReport().getId(),
                source.getUser() == null ? null : source.getUser().getId(),
                source.getReportJob() == null? null : source.getReportJob().getId(),
                source.getOlapConfiguration().getId(),
                source.getCreator().getId(),
                source.getIsDefault(),
                source.getIsShared(),
                source.getIsCurrent(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()

        );
    }
}
