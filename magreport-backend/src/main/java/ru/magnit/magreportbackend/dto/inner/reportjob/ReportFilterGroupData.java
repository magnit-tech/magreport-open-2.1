package ru.magnit.magreportbackend.dto.inner.reportjob;

import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;

import java.util.LinkedList;
import java.util.List;

public record ReportFilterGroupData(
        Long id,
        Long parentId,
        String code,
        String parentCode,
        BinaryBooleanOperations operationType,
        List<ReportFilterData> filters,
        List<ReportFilterGroupData> groups
) {

    public List<ReportFilterData> getAllFilters() {
        var result = new LinkedList<>(filters);
        if (groups.isEmpty()) return result;
        result.addAll(groups.stream().flatMap(group -> group.getAllFilters().stream()).toList());
        return result;
    }
}
