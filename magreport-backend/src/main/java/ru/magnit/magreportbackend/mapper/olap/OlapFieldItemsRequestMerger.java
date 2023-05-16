package ru.magnit.magreportbackend.mapper.olap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequestNew;
import ru.magnit.magreportbackend.mapper.Merger2;
@Service
@RequiredArgsConstructor
public class OlapFieldItemsRequestMerger implements Merger2<OlapFieldItemsRequest, OlapFieldItemsRequestNew, OlapCubeRequest> {
    @Override
    public OlapFieldItemsRequest merge(OlapFieldItemsRequestNew source1, OlapCubeRequest source2) {
        return new OlapFieldItemsRequest()
                .setFieldId(source1.getFieldId())
                .setFrom(source1.getFrom())
                .setCount(source1.getCount())
                .setJobId(source1.getJobId())
                .setColumnFields(source2.getColumnFields())
                .setRowFields(source2.getRowFields())
                .setMetrics(source2.getMetrics())
                .setFilterGroup(source2.getFilterGroup())
                .setMetricFilterGroup(source2.getMetricFilterGroup());
    }
}
