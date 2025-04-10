package ru.magnit.magreportbackend.dto.backup.filterinstance;

import java.time.LocalDateTime;

public record FilterInstanceFieldBackupTuple(

        Long filterInstanceFieldId,
        Long level,
        Long filterInstanceId,
        Long filterTemplateFieldId,
        Long datasetFieldId,
        boolean expand,
        String name,
        String description,
        Boolean showField,
        Boolean searchByField,
        LocalDateTime created,
        LocalDateTime modified
){ }
