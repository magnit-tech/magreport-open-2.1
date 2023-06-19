package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportField;
import ru.magnit.magreportbackend.dto.inner.filter.FilterFieldRequestData;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@AllArgsConstructor
public class FilterFieldRequestFromFRFMapper implements Mapper<FilterFieldRequestData, FilterReportField> {
    @Override
    public FilterFieldRequestData from(FilterReportField source) {
        return new FilterFieldRequestData(
                source.getId(),
                source.getFilterInstanceField().getDataSetField().getName(),
                DataTypeEnum.getTypeByOrdinal(source.getFilterInstanceField().getDataSetField().getType().getId().intValue()),
                source.getFilterInstanceField().getShowField(),
                source.getFilterInstanceField().getSearchByField());
    }
}
