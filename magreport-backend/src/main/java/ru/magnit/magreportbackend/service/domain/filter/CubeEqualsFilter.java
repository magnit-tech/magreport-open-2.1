package ru.magnit.magreportbackend.service.domain.filter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.request.olap.FilterDefinition;
import ru.magnit.magreportbackend.exception.InvalidParametersException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CubeEqualsFilter implements CubeFilterNode {

    private boolean invert;
    private String value;
    private String[] data;
    private DataTypeEnum dataType;
    private int rounding;
    private boolean isRounding;


    @Override
    public CubeFilterNode init(CubeData sourceCube, FilterDefinition filterDefinition) {
        invert = filterDefinition.isInvertResult();
        value = filterDefinition.getValues().isEmpty() ? "" : filterDefinition.getValues().get(0).intern();
        data = sourceCube.data()[sourceCube.fieldIndexes().get(filterDefinition.getFieldId())];
        dataType = sourceCube.reportMetaData().getTypeField(filterDefinition.getFieldId());
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
                    yield BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).equals(BigDecimal.valueOf(filter).setScale(rounding, RoundingMode.HALF_UP));
                else
                    yield current == filter;
            }

            case DATE -> {
                var current = data[row] == null ? LocalDate.MIN : LocalDate.parse(data[row]);
                var filter = value.isEmpty() ? LocalDate.MIN : LocalDate.parse(value);
                yield current.isEqual(filter);
            }
            case TIMESTAMP -> {
                var current = data[row] == null ? LocalDateTime.MIN : LocalDateTime.parse(data[row].replace(" ", "T"));
                var filter = value.isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(value.replace(" ", "T"));
                yield current.isEqual(filter);
            }

            case STRING -> {
                var current = data[row] == null ? "" : data[row];
                yield current.equalsIgnoreCase(value);
            }

            case BOOLEAN -> {
                var current = data[row] != null && Boolean.parseBoolean(data[row]);
                var filter = !value.isEmpty() && Boolean.parseBoolean(value);
                yield current &&  filter;
            }

            case UNKNOWN -> throw new InvalidParametersException("Not supported datatype field");
        };

        return invert != result;
    }

}
