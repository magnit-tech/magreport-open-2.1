package ru.magnit.magreportbackend.mapper.serversettings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.reportjob.JobToken;
import ru.magnit.magreportbackend.dto.backup.serversettings.JobTokenBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class JobTokenBackupMapper implements Mapper<JobTokenBackupTuple, JobToken> {
    @Override
    public JobTokenBackupTuple from(JobToken source) {
        return new JobTokenBackupTuple(
                source.getToken(),
                source.getReportJobId(),
                source.getExcelTemplateId(),
                source.getEmail(),
                source.getCreated()
        );
    }
}
