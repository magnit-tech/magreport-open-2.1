package ru.magnit.magreportbackend.mapper.filterinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterFieldTypeEnum;
import ru.magnit.magreportbackend.dto.inner.filter.FilterValueListRequestData;
import ru.magnit.magreportbackend.dto.request.filterinstance.ListValuesRequest;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.mapper.Merger2;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceViewMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterInstanceQueryDataMerger implements Merger2<FilterValueListRequestData, FilterInstance, ListValuesRequest> {

    private final DataSourceViewMapper dataSourceMapper;
    private final FilterFieldRequestDataFromFIFMapper filterFieldRequestDataFromFIFMapper;

    @Override
    public FilterValueListRequestData merge(FilterInstance source1, ListValuesRequest source2) {
        var filterFields = getFilterField(source1);
        var idField = getFilterFieldByType(source1, FilterFieldTypeEnum.ID_FIELD);
        var codeField = getFilterFieldByType(source1, FilterFieldTypeEnum.CODE_FIELD);
        var nameFields = getAllFilterFieldByType(source1, FilterFieldTypeEnum.NAME_FIELD);

        return new FilterValueListRequestData(
                dataSourceMapper.from(source1.getDataSet().getDataSource()),
                source1.getDataSet().getSchemaName(),
                source1.getDataSet().getObjectName(),
                filterFieldRequestDataFromFIFMapper.from(filterFields),
                filterFieldRequestDataFromFIFMapper.from(idField),
                filterFieldRequestDataFromFIFMapper.from(codeField),
                filterFieldRequestDataFromFIFMapper.from(nameFields),
                !source2.getIsCaseInsensitive(),
                source2.getLikenessType(),
                source2.getMaxCount(),
                source2.getSearchValue()
        );
    }

    private FilterInstanceField getFilterFieldByType(FilterInstance source1, FilterFieldTypeEnum fieldType) {
        return source1.getFields()
                .stream()
                .filter(o -> o.getTemplateField().getType().getId().equals((long) fieldType.ordinal()))
                .findFirst()
                .orElseThrow(() -> new InvalidParametersException("There is no field with type:" + fieldType + " in filter with id:" + source1.getId()));
    }

    private List<FilterInstanceField> getAllFilterFieldByType(FilterInstance source1, FilterFieldTypeEnum fieldType) {
        return source1.getFields()
                .stream()
                .filter(o -> o.getTemplateField().getType().getId().equals((long) fieldType.ordinal()))
                .filter(o -> o.getShowField() != null && o.getShowField())
                .toList();
    }

    private List<FilterInstanceField> getFilterField(FilterInstance source1) {
        return source1.getFields()
                .stream()
                .filter(f -> f.getSearchByField() != null && f.getSearchByField())
                .toList();
    }
}