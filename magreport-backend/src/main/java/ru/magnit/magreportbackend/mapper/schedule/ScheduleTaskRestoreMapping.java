package ru.magnit.magreportbackend.mapper.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.ExcelTemplate;
import ru.magnit.magreportbackend.domain.report.Report;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTask;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTaskStatus;
import ru.magnit.magreportbackend.domain.schedule.ScheduleTaskType;
import ru.magnit.magreportbackend.dto.backup.schedule.ScheduleTaskBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ScheduleTaskRestoreMapping implements Mapper<ScheduleTask, ScheduleTaskBackupTuple> {
    @Override
    public ScheduleTask from(ScheduleTaskBackupTuple source) {
        return new ScheduleTask()
                .setId(source.scheduleTaskId())
                .setStatus(new ScheduleTaskStatus(source.scheduleTaskStatusId()))
                .setTaskType(new ScheduleTaskType(source.scheduleTaskTypeId()))
                .setReport(new Report(source.reportId()))
                .setRenewalPeriod(source.renewalPeriod())
                .setExcelTemplate(new ExcelTemplate(1L))
                .setMaxFailedStart(source.maxFailedStarts())
                .setFailedStart(source.failedStarts())
                .setExpirationCode(source.expirationCode())
                .setCode(source.code())
                .setName(source.name())
                .setDescription(source.description())
                .setReportTitleMail(source.reportTitleMail())
                .setErrorBodyMail(source.errorBodyMail())
                .setReportBodyMail(source.reportBodyMail())
                .setSendEmptyReport(source.sendEmptyReport())
                .setExpirationDate(source.expirationDate());
    }
}
