package ru.magnit.magreportbackend.dto.response.securityfilter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterOperationTypeEnum;
import ru.magnit.magreportbackend.dto.response.filterinstance.FilterInstanceResponse;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.magnit.magreportbackend.util.Constant.ISO_DATE_TIME_PATTERN;
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityFilterResponse{

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        Long id;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        Long folderId;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        FilterInstanceResponse filterInstance;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        FilterOperationTypeEnum operationType;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        String name;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        String description;

        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        List<SecurityFilterDataSetResponse> dataSets;

        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        List<RoleSettingsResponse> roleSettings;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        String userName;

        @JsonFormat(pattern = ISO_DATE_TIME_PATTERN)
        LocalDateTime created;

        @JsonFormat(pattern = ISO_DATE_TIME_PATTERN)
        LocalDateTime modified;

        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        List<FolderNodeResponse> path = Collections.emptyList();

}
