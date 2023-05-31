package ru.magnit.magreportbackend.service.domain.filter;

import ru.magnit.magreportbackend.domain.enums.BinaryBooleanOperations;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.request.olap.FilterGroup;

import java.util.ArrayList;
import java.util.List;

public class CubeFilterGroup implements CubeFilterNode{

    private final List<CubeFilterNode> childNodes = new ArrayList<>();
    private BinaryBooleanOperations operationType;
    private boolean invertResult;

    @Override
    public CubeFilterNode init(CubeData sourceCube, FilterGroup filterGroup) {
        if (filterGroup.getChildGroups() != null && !filterGroup.getChildGroups().isEmpty())
            childNodes.addAll(filterGroup.getChildGroups().stream().map(childGroup -> CubeFilters.createFilter(sourceCube, childGroup)).toList());

        if (!filterGroup.getFilters().isEmpty())
            childNodes.addAll(filterGroup.getFilters().stream().map(filter -> CubeFilters.createFilter(sourceCube, filter)).toList());

        operationType = filterGroup.getOperationType();
        invertResult = filterGroup.isInvertResult();
        return this;
    }

    @Override
    public boolean filter(int row) {
        boolean result;
        if (operationType == BinaryBooleanOperations.AND) {
            result = true;
            for (CubeFilterNode childNode : childNodes) {
                result &= childNode.filter(row);
            }
        } else {
            result = false;
            for (CubeFilterNode childNode : childNodes) {
                result |= childNode.filter(row);
            }
        }
        return invertResult != result;
    }
}
