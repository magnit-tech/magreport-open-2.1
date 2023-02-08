package ru.magnit.magreportbackend.dto.backup;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class RestoreMappingObject {
    BackupObjectTypeEnum type;
    Long id;
    Long newId;
}
