package ru.magnit.magreportbackend.dto.backup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
public class BackupRestoreRequest {

    List<Long> dataSets = Collections.emptyList();
    List<Long> dataSources = Collections.emptyList();
    List<Long> reports = Collections.emptyList();
    List<Long> filterInstances = Collections.emptyList();
    List<Long> schedules = Collections.emptyList();
    List<Long> scheduleTasks = Collections.emptyList();
    List<Long> themes = Collections.emptyList();
    List<Long> securityFilters = Collections.emptyList();
    List<RestoreMappingObject> mapping = Collections.emptyList();

}