package ru.magnit.magreportbackend.service.domain.filter;

import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.olap.CubeData;
import ru.magnit.magreportbackend.dto.request.olap.FilterDefinition;
import ru.magnit.magreportbackend.exception.InvalidParametersException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CubeBetweenFilter implements CubeFilterNode {

    private boolean invert;
    private List<String> values;
    private String[] data;
    private DataTypeEnum dataType;
    private int rounding;
    private boolean isRounding;

    @Override
    public CubeFilterNode init(CubeData sourceCube, FilterDefinition filterDefinition) {
        dataType = sourceCube.reportMetaData().getTypeField(filterDefinition.getFieldId());
        invert = filterDefinition.isInvertResult();
        values = filterDefinition.getValues().stream().sorted().toList();
        data = sourceCube.data()[sourceCube.fieldIndexes().get(filterDefinition.getFieldId())];
        if (values.size() != 2) throw new InvalidParametersException("Incorrect number of filter arguments");
        rounding = filterDefinition.getRounding();
        isRounding = filterDefinition.isCanRounding();
        return this;
    }

    @Override
    public boolean filter(int row) {
        var result = switch (dataType) {
            case INTEGER, DOUBLE -> {
                var current = data[row] == null ? Double.MIN_VALUE : Double.parseDouble(data[row]);
                var filterStart = values.get(0).isEmpty() ? Double.MIN_VALUE : Double.parseDouble(values.get(0));
                var filterStop = values.get(1).isEmpty() ? Double.MIN_VALUE : Double.parseDouble(values.get(1));
            if (isRounding)
                yield BigDecimal.valueOf(filterStart).setScale(rounding, RoundingMode.HALF_UP).doubleValue() <=
                        BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).doubleValue() &&
                        BigDecimal.valueOf(current).setScale(rounding, RoundingMode.HALF_UP).doubleValue() <=
                                BigDecimal.valueOf(filterStop).setScale(rounding, RoundingMode.HALF_UP).doubleValue();
            else
                yield  filterStart <= current && current <= filterStop;
            }

            case DATE -> {
                var current = data[row] == null  ? LocalDate.MIN : LocalDate.parse(data[row]);
                var filterStart = values.get(0).isEmpty() ? LocalDate.MIN : LocalDate.parse(values.get(0));
                var filterStop = values.get(1).isEmpty() ? LocalDate.MAX : LocalDate.parse(values.get(1));
                yield (current.isAfter(filterStart) || current.isEqual(filterStart)) && (current.isBefore(filterStop) || current.isEqual(filterStop));

            }

            case TIMESTAMP -> {
                var current = data[row].isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(data[row].replace(" ", "T"));
                var filterStart = values.get(0).isEmpty() ? LocalDateTime.MIN : LocalDateTime.parse(values.get(0).replace(" ", "T"));
                var filterStop = values.get(1).isEmpty() ? LocalDateTime.MAX : LocalDateTime.parse(values.get(1).replace(" ", "T"));
                yield (current.isAfter(filterStart) || current.isEqual(filterStart)) && (current.isBefore(filterStop) || current.isEqual(filterStop));
            }

            case STRING, BOOLEAN ->  throw new InvalidParametersException("Not supported datatype field");
        };


        return invert != result;
    }
}
