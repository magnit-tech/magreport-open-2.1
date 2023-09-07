package ru.magnit.magreportbackend.dto.request.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.dto.request.folder.FolderTypes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RolePermissionDeleteRequest {

    private Long folderId;
    private Long roleId;
    private FolderTypes type;
}
