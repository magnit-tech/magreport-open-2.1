package ru.magnit.magreportbackend.mapper.olap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequestNew;
import ru.magnit.magreportbackend.dto.request.olap.OlapFieldItemsRequestNew;
import ru.magnit.magreportbackend.mapper.Mapper;
@Service
@RequiredArgsConstructor
public class OlapCubeRequestMapper implements Mapper<OlapCubeRequestNew,OlapFieldItemsRequestNew> {
    @Override
    public OlapCubeRequestNew from(OlapFieldItemsRequestNew source) {
        return new OlapCubeRequestNew()
                .setJobId(source.getJobId())
                .setColumnFields(source.getColumnFields())
                .setRowFields(source.getRowFields())
                .setFilterGroup(source.getFilterGroup())
                .setMetrics(source.getMetrics())
                .setMetricFilterGroup(source.getMetricFilterGroup());
    }
}
