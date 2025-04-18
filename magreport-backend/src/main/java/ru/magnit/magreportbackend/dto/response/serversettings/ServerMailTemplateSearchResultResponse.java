package ru.magnit.magreportbackend.dto.response.serversettings;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;

import java.util.List;

public record ServerMailTemplateSearchResultResponse(
        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        List<FolderNodeResponse> path,

        @JsonFormat(shape = JsonFormat.Shape.OBJECT)
        ServerMailTemplateResponse element
) {
}
