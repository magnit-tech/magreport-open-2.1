package ru.magnit.magreportbackend.service.domain.filter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.request.olap.FilterDefinition;
import ru.magnit.magreportbackend.exception.InvalidParametersException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CubeLesserFilter implements CubeFilterNode {

    private boolean invert;
    private String value;
    private String[] data;
    private DataTypeEnum dataType;
    private int rounding;
    private boolean isRounding;

    @Override
    public CubeFilterNode init(CubeData sourceCube, FilterDefinition filterDefinition) {
        dataType = sourceCube.reportMetaData().getTypeField(filterDefinition.getFieldId());
        invert = filterDefinition.isInvertResult();
        value = filterDefinition.getValues().isEmpty() ? "" : filterDefinition.getValues().get(0);
        data = sourceCube.data()[sourceCube.fieldIndexes().get(filterDefinition.getFieldId())];
        rounding = filterDefinition.getRounding();
        isRounding = filterDefinition.isCanRounding();
        return this;
    }

    @Override
    public boolean filter(int row) {
        var result = switch (dataType) {
            case INTEGER, DOUBLE -> {
                var current = data[row] == null ? Double.MIN_VALUE : Double.parseDouble(data[row]);
                var filter = value.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(value);
                if (isRounding)
                    yield BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).doubleValue() <
                            BigDecimal.valueOf(filter).setScale(rounding, RoundingMode.HALF_UP).doubleValue();
                else
                    yield current < filter;
            }

            case DATE -> {
                var current = data[row] == null ? LocalDate.MIN : LocalDate.parse(data[row]);
                var filter = value.isEmpty() ? LocalDate.MIN : LocalDate.parse(value);
                yield current.isBefore(filter);
            }
            case TIMESTAMP -> {
                var current = data[row] == null ? LocalDateTime.MIN : LocalDateTime.parse(data[row].replace(" ", "T"));
                var filter = value.isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(value.replace(" ", "T"));
                yield current.isBefore(filter);
            }
            case STRING, BOOLEAN, UNKNOWN -> throw new InvalidParametersException("Not supported datatype field");
        };

        return invert != result;
    }
}
