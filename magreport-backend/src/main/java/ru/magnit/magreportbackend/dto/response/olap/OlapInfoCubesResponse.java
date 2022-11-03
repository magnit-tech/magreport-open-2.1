package ru.magnit.magreportbackend.dto.response.olap;

import java.time.LocalDateTime;

public record OlapInfoCubesResponse(

        Long reportId,
        String reportName,
        Long reportJobId,
        Long rowCount,
        String username,
        Long useCount,
        LocalDateTime created,
        LocalDateTime modified

) {

}
