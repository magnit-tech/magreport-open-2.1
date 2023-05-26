package ru.magnit.magreportbackend.dto.inner.filter;

import ru.magnit.magreportbackend.dto.inner.datasource.DataSourceData;
import ru.magnit.magreportbackend.dto.request.filterinstance.LikenessType;

import java.util.List;

public record FilterValueListRequestData(
        DataSourceData dataSource,
        String schemaName,
        String tableName,
        List<FilterFieldRequestData> filterFields,
        FilterFieldRequestData idField,
        FilterFieldRequestData codeField,
        List<FilterFieldRequestData> nameFields,
        boolean isCaseSensitive,
        LikenessType likenessType,
        long maxCount,
        String searchValue
) {}
