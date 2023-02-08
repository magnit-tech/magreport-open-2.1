package ru.magnit.magreportbackend.mapper.reportjob;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterOperationTypeEnum;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTypeEnum;
import ru.magnit.magreportbackend.domain.reportjob.ReportJobFilter;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportJobFilterData;
import ru.magnit.magreportbackend.mapper.Mapper;

@Service
@RequiredArgsConstructor
public class ReportJobFilterDataMapper implements Mapper<ReportJobFilterData, ReportJobFilter> {

    private final ReportJobTupleDataMapper tupleDataMapper;

    @Override
    public ReportJobFilterData from(ReportJobFilter source) {
        final var dataset = source.getFilterReport().getFilterInstance().getDataSet();
        final var datasetId = dataset == null ? null : dataset.getId();
        return new ReportJobFilterData(
                source.getFilterReport().getId(),
                datasetId,
                -1L,
                "NONE",
                FilterTypeEnum.getByOrdinal(source.getFilterReport().getFilterInstance().getFilterTemplate().getType().getId()),
                FilterOperationTypeEnum.getById(source.getFilterOperationType().getId()),
                source.getFilterReport().getCode(),
                source.getFilterReport().getMaxCountItems(),
                source.getFilterReport().getName(),
                tupleDataMapper.from(source.getTuples()));
    }
}
