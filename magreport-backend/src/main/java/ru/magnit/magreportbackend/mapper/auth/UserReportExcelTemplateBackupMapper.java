package ru.magnit.magreportbackend.mapper.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.excel.UserReportExcelTemplate;
import ru.magnit.magreportbackend.dto.backup.user.UserReportExcelTemplateBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class UserReportExcelTemplateBackupMapper implements Mapper<UserReportExcelTemplateBackupTuple, UserReportExcelTemplate> {
    @Override
    public UserReportExcelTemplateBackupTuple from(UserReportExcelTemplate source) {
        return new UserReportExcelTemplateBackupTuple(
                source.getId(),
                source.getUser().getId(),
                source.getReport().getId(),
                source.getReportExcelTemplate().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
