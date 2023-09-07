package ru.magnit.magreportbackend.mapper.filterreport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.magnit.magreportbackend.domain.dataset.DataSet;
import ru.magnit.magreportbackend.domain.dataset.DataSetField;
import ru.magnit.magreportbackend.domain.dataset.DataType;
import ru.magnit.magreportbackend.domain.datasource.DataSource;
import ru.magnit.magreportbackend.domain.datasource.DataSourceTypeEnum;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstance;
import ru.magnit.magreportbackend.domain.filterinstance.FilterInstanceField;
import ru.magnit.magreportbackend.domain.filterreport.FilterReport;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportField;
import ru.magnit.magreportbackend.domain.filterreport.FilterReportGroup;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterFieldType;
import ru.magnit.magreportbackend.domain.filtertemplate.FilterTemplateField;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.inner.datasource.DataSourceData;
import ru.magnit.magreportbackend.dto.inner.filter.FilterFieldRequestData;
import ru.magnit.magreportbackend.dto.request.filterinstance.LikenessType;
import ru.magnit.magreportbackend.dto.request.filterinstance.ListValuesRequest;
import ru.magnit.magreportbackend.mapper.datasource.DataSourceViewMapper;
import ru.magnit.magreportbackend.mapper.filterinstance.FilterFieldRequestFromFRFMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.magnit.magreportbackend.domain.dataset.DataTypeEnum.STRING;

@ExtendWith(MockitoExtension.class)
class FilterReportQueryDataMergerTest {

    @Mock
    private DataSourceViewMapper dataSourceViewMapper;

    @Mock
    private FilterFieldRequestFromFRFMapper filterFieldRequestFromFRFMapper;

    @InjectMocks
    private FilterReportQueryDataMerger merger;

    private final static Long ID = 1L;
    private final static String NAME = "Name";
    private final static String DESCRIPTION = "Description";
    private final static String CODE = "Code";
    private final static String SCHEMA = "Schema";
    private final static String OBJECT = "Object";
    private final static Boolean HIDDEN = false;
    private final static Boolean MANDATORY = false;
    private final static Boolean ROOT = false;
    private final static Long ORDINAL = 1L;
    private final static LocalDateTime CREATE_TIME = LocalDateTime.now();
    private final static LocalDateTime MODIFIED_TIME = LocalDateTime.now().plusDays(1);

    @Test
    void merge() {

        when(dataSourceViewMapper.from(any(DataSource.class))).thenReturn(getDataSourceData());
        when(filterFieldRequestFromFRFMapper.from(anyList())).thenReturn(Collections.singletonList(getFilterFieldRequestData()));
        when(filterFieldRequestFromFRFMapper.from(any(FilterReportField.class))).thenReturn(getFilterFieldRequestData());

        var response = merger.merge(getFilterReport(), getListValuesRequest());

        assertEquals(getDataSourceData(), response.dataSource());
        assertEquals(SCHEMA, response.schemaName());
        assertEquals(OBJECT, response.tableName());
        assertEquals(NAME, response.filterFields().get(0).getFieldName());
        assertEquals(STRING, response.filterFields().get(0).getFieldType());
        assertEquals(1L, response.idField().getFieldId());
        assertEquals(NAME, response.idField().getFieldName());
        assertEquals(1L, response.codeField().getFieldId());
        assertEquals(NAME, response.codeField().getFieldName());
        assertEquals(1L, response.nameFields().get(0).getFieldId());
        assertEquals(NAME, response.nameFields().get(0).getFieldName());
        assertFalse(response.isCaseSensitive());
        assertEquals(LikenessType.CONTAINS, response.likenessType());
        assertEquals(5L, response.maxCount());
        assertEquals("", response.searchValue());

        verify(dataSourceViewMapper).from(any(DataSource.class));
        verifyNoMoreInteractions(dataSourceViewMapper);
    }

    private FilterReport getFilterReport() {
        return new FilterReport()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setHidden(HIDDEN)
                .setMandatory(MANDATORY)
                .setCode(CODE)
                .setOrdinal(ORDINAL)
                .setUser(new User())
                .setReportJobFilters(Collections.emptyList())
                .setGroup(new FilterReportGroup())
                .setFields(Arrays.asList(
                        getFilterReportField(10L, new FilterFieldType().setId(0L)),
                        getFilterReportField(20L, new FilterFieldType().setId(1L)),
                        getFilterReportField(30L, new FilterFieldType().setId(2L))))
                .setFilterInstance(
                        new FilterInstance()
                                .setDataSet(
                                        new DataSet()
                                                .setSchemaName(SCHEMA)
                                                .setObjectName(OBJECT)
                                                .setDataSource(new DataSource())))
                .setRootSelectable(ROOT)
                .setCreatedDateTime(CREATE_TIME)
                .setModifiedDateTime(MODIFIED_TIME);
    }

    private ListValuesRequest getListValuesRequest() {

        var request = new ListValuesRequest();

        request.setFilterId(ID);
        request.setFilterFieldId(10L);
        request.setIsCaseInsensitive(true);
        request.setSearchValue("");
        request.setLikenessType(LikenessType.CONTAINS);
        request.setMaxCount(5L);
        return request;
    }

    private FilterReportField getFilterReportField(Long id, FilterFieldType type) {
        return new FilterReportField()
                .setId(id)
                .setFilterInstanceField(
                        new FilterInstanceField()
                                .setDataSetField(
                                        new DataSetField()
                                                .setName(NAME + id)
                                                .setType(new DataType().setId(ID)))
                                .setTemplateField(
                                        new FilterTemplateField()
                                                .setId(ID)
                                                .setType(type)
                                )
                );
    }

    private DataSourceData getDataSourceData() {
        return new DataSourceData(ID, DataSourceTypeEnum.IMPALA, "url", "username", "******", (short) 5, "name");
    }

    private FilterFieldRequestData getFilterFieldRequestData() {
        return new FilterFieldRequestData(
                ID,
                NAME,
                STRING,
                Boolean.TRUE,
                Boolean.TRUE
        );
    }
}
