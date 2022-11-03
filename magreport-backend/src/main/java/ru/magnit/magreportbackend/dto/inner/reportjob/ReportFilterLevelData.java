package ru.magnit.magreportbackend.dto.inner.reportjob;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;

public record ReportFilterLevelData(
        long idFieldId,
        long dataSetFieldId,
        long codeFieldId,
        long dataSetCodeFieldId,
        DataTypeEnum reportFieldType,
        String reportFieldName,
        String filterIdFieldName,
        String filterCodeFieldName,
        DataTypeEnum filterFieldType
) {}
