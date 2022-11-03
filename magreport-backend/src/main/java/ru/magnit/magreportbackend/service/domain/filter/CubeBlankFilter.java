package ru.magnit.magreportbackend.service.domain.filter;

import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.request.olap.FilterDefinition;

public class CubeBlankFilter implements CubeFilterNode{

    private boolean invert;
    private String[] data;

    @Override
    public CubeFilterNode init(CubeData sourceCube, FilterDefinition filterDefinition) {
        invert = filterDefinition.isInvertResult();
        data = sourceCube.data()[sourceCube.fieldIndexes().get(filterDefinition.getFieldId())];
        return this;
    }

    @Override
    public boolean filter(int row) {
        return invert != (data[row] == null);
    }
}
