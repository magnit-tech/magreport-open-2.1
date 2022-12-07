package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobUser;
import ru.magnit.magreportbackend.dto.backup.reportjob.ReportJobUserBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobUserBackupMapper implements Mapper<ReportJobUserBackupTuple, ReportJobUser> {
    @Override
    public ReportJobUserBackupTuple from(ReportJobUser source) {
        return new ReportJobUserBackupTuple(
                source.getReportJob().getId(),
                source.getUser().getId(),
                source.getId(),
                source.getType().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
