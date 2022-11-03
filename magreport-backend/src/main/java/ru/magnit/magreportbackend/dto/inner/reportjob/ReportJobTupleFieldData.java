package ru.magnit.magreportbackend.dto.inner.reportjob;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;

public record ReportJobTupleFieldData(
        Long fieldId,

        Long datasetId,
        Long datasetFieldId,
        Long level,
        String fieldName,
        DataTypeEnum fieldDataType,
        String value
) {}
