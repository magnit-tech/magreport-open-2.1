package ru.magnit.magreportbackend.dto.response.datasource;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.magnit.magreportbackend.util.Constant.ISO_DATE_TIME_PATTERN;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceResponse {

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        private Long id;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        Long folderId,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private String name;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private String description;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private String url;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private String userName;

        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        private DataSourceTypeResponse type;

        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        private Short poolSize;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private String creator;

        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        private List<FolderNodeResponse> path = Collections.emptyList();

        @JsonFormat(pattern = ISO_DATE_TIME_PATTERN)
        private LocalDateTime created;

        @JsonFormat(pattern = ISO_DATE_TIME_PATTERN)
        private LocalDateTime modified;

}
