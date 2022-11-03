package ru.magnit.magreportbackend.mapper.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.favorite.FavReport;
import ru.magnit.magreportbackend.dto.backup.report.FavReportBackupTuple;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class FavReportBackupMapper implements Mapper<FavReportBackupTuple, FavReport> {
    @Override
    public FavReportBackupTuple from(FavReport source) {
        return new FavReportBackupTuple(
                source.getId(),
                source.getUser().getId(),
                source.getFolder().getId(),
                source.getReport().getId(),
                source.getCreatedDateTime(),
                source.getModifiedDateTime()
        );
    }
}
