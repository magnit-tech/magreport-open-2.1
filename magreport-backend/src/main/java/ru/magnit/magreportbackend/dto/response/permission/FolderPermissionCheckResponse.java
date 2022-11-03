package ru.magnit.magreportbackend.dto.response.permission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.domain.folderreport.FolderAuthorityEnum;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class FolderPermissionCheckResponse {

    FolderAuthorityEnum authority;

    public FolderPermissionCheckResponse(FolderAuthorityEnum authority) {
        this.authority = authority;
    }
}

