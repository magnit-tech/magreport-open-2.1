package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.dto.inner.filter.FilterFieldRequestData;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@AllArgsConstructor
public class FilterFieldRequestDataFromFIFMapper implements Mapper<FilterFieldRequestData, FilterInstanceField> {
    @Override
    public FilterFieldRequestData from(FilterInstanceField source) {
        return new FilterFieldRequestData(
                source.getId(),
                source.getDataSetField().getName(),
                DataTypeEnum.getTypeByOrdinal(source.getDataSetField().getType().getId().intValue()),
                source.getShowField(),
                source.getSearchByField()
        );
    }
}
