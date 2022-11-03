package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTask;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleTaskBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleTaskBackupMapper implements Mapper<ScheduleTaskBackupTuple, ScheduleTask> {
    @Override
    public ScheduleTaskBackupTuple from(ScheduleTask source) {
        return new ScheduleTaskBackupTuple(
                source.getId(),
                source.getReport().getId(),
                source.getTaskType().getId(),
                source.getStatus().getId(),
                source.getRenewalPeriod(),
                source.getExcelTemplate().getId(),
                source.getUser().getId(),
                source.getFailedStart(),
                source.getMaxFailedStart(),
                source.getExpirationCode(),
                source.getCode(),
                source.getReportBodyMail(),
                source.getName(),
                source.getDescription(),
                source.getReportTitleMail(),
                source.getErrorBodyMail(),
                source.getErrorTitleMail(),
                source.getSendEmptyReport(),
                source.getSendEmptyReport(),
                source.getExpirationDate(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
