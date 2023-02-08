package ru.magnit.magreportbackend.service.domain.converter.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.units.qual.A;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldMetadataResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobMetadataResponse;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.domain.converter.Writer;
import ru.magnit.magreportbackend.service.telemetry.TelemetryService;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PivotTableWriter2 implements Writer {


    private final OlapCubeResponse data;
    private final OlapExportPivotTableRequest request;
    private final ReportJobMetadataResponse metadata;
    private final Map<String, Object> config;
    private final TelemetryService telemetryService;
    private final Path exportPath;
    private final String nameDataList;

    private int shiftRowCount;
    private int shiftColCount;
    private boolean mergeMode = false;
    private boolean columnsMetricPlacement = false;

    private Map<Long, String> mappingFields;
    private Map<Long, String> typeFields;

    private static final short META_COLOR = IndexedColors.LIGHT_CORNFLOWER_BLUE.index;
    private static final short MEASURE_COLOR = IndexedColors.LEMON_CHIFFON.index;
    private static final short METRIC_COLOR = IndexedColors.WHITE.index;

    private CellStyle dateCellMetaStyle;
    private CellStyle dateCellMeasureStyle;
    private CellStyle dateCellMetricStyle;

    private CellStyle numericCellMetaStyle;
    private CellStyle numericCellMeasureStyle;
    private CellStyle numericCellMetricStyle;

    private CellStyle integerCellMetaStyle;
    private CellStyle integerCellMeasureStyle;
    private CellStyle integerCellMetricStyle;

    private CellStyle textCellMetaStyle;
    private CellStyle textCellMeasureStyle;
    private CellStyle textCellMetricStyle;

    private int totalColumn;
    private int totalRow;
    private List<String> rowMetaNames = Collections.emptyList();
    private List<String> columnMetaNames = Collections.emptyList();

    private static final String ERROR_COLOR_TEXT = "Unknown color excel pivot table:";


    @Override
    public void convert(String templatePath) {

        try (
                final var inputStream = Files.newInputStream(Paths.get(templatePath));
                final var workbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream), SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
                final var outputStream = new BufferedOutputStream(new FileOutputStream(exportPath.toFile()))
        ) {

            initConfig(workbook);
            var sheet = getSheet(workbook);

            var mergeCol = new ArrayList<MergeData>();
            var mergeRow = new ArrayList<MergeData>();

            for (int i = 0; i < shiftRowCount; i++){
                var mergeObject = new MergeData(i, i, shiftColCount, shiftColCount,"");
                mergeRow.add(mergeObject);
            }


            for (int i = 0; i < shiftColCount; i++){
                var mergeObject = new MergeData(shiftRowCount, shiftRowCount, i, i,"");
                mergeCol.add(mergeObject);
            }

            for (int indexRow = 0 ; indexRow < totalRow; indexRow++) {
                var row = sheet.createRow(indexRow);

                for (int indexCol = 0; indexCol < totalColumn; indexCol++) {

                    var cell = row.createCell(indexCol);

                    if (indexRow < shiftRowCount){

                        if (indexCol < shiftColCount){
                           cell.setCellValue(rowMetaNames.get(indexCol));
                        } else {
                            cell.setCellValue(data.getRowValues().get(indexRow).get(indexCol - shiftColCount));
                        }

                    } else
                        cell.setCellValue(0);



                }
            }


            workbook.write(outputStream);
            workbook.dispose();

        } catch (Exception ex) {
            throw new ReportExportException("Error export report to excel file", ex);
        }

    }

    private SXSSFSheet getSheet(SXSSFWorkbook workbook) {
        var sheet = workbook.getSheet(nameDataList);

        if (sheet != null) {
            var indexSheet = workbook.getSheetIndex(sheet);
            workbook.removeSheetAt(indexSheet);
        }

        return workbook.createSheet(nameDataList);
    }

    private void initConfig(Workbook wb) {

        if (config.containsKey("mergeMode")) mergeMode = (boolean) config.get("mergeMode");
        if (config.containsKey("columnsMetricPlacement"))
            columnsMetricPlacement = (boolean) config.get("columnsMetricPlacement");

        initValues();
        initCellStyles(wb);

        mappingFields = metadata.fields().stream().collect(Collectors.toMap(ReportFieldMetadataResponse::id, ReportFieldMetadataResponse::name));
        typeFields = metadata.fields().stream().collect(Collectors.toMap(ReportFieldMetadataResponse::id, ReportFieldMetadataResponse::type));

        OlapCubeRequest cubeRequest = request.getCubeRequest();

        rowMetaNames = cubeRequest.getRowFields().stream().filter(mappingFields::containsKey).map(mappingFields::get).toList();
        columnMetaNames = cubeRequest.getColumnFields().stream().filter(mappingFields::containsKey).map(mappingFields::get).toList();

        if (columnsMetricPlacement && !data.getMetricValues().isEmpty())
            totalColumn = data.getTotalColumns() * data.getMetricValues().size() + shiftColCount;
        else totalColumn = data.getTotalColumns() + shiftColCount;
    }

    private void initCellStyles(Workbook wb) {
        dateCellMetaStyle = initCellStyle(wb, (short) 14, META_COLOR);
        dateCellMeasureStyle = initCellStyle(wb, (short) 14, MEASURE_COLOR);
        dateCellMetricStyle = initCellStyle(wb, (short) 14, METRIC_COLOR);

        numericCellMetaStyle = initCellStyle(wb, (short) 4, META_COLOR);
        numericCellMeasureStyle = initCellStyle(wb, (short) 4, MEASURE_COLOR);
        numericCellMetricStyle = initCellStyle(wb, (short) 4, METRIC_COLOR);

        integerCellMetaStyle = initCellStyle(wb, (short) 1, META_COLOR);
        integerCellMeasureStyle = initCellStyle(wb, (short) 1, MEASURE_COLOR);
        integerCellMetricStyle = initCellStyle(wb, (short) 1, METRIC_COLOR);

        textCellMetaStyle = initCellStyle(wb, (short) 0x31, META_COLOR);
        textCellMeasureStyle = initCellStyle(wb, (short) 0x31, MEASURE_COLOR);
        textCellMetricStyle = initCellStyle(wb, (short) 0x31, METRIC_COLOR);
    }

    private void initValues(){

        if (columnsMetricPlacement){

            shiftRowCount =  data.getColumnValues().get(0).size();
            shiftColCount =  data.getRowValues().get(0).size();

            totalColumn = (shiftColCount == 0 ? 1 : shiftColCount) + data.getTotalColumns();
            totalRow =  shiftRowCount + data.getTotalRows() + 1;
        } else {
            shiftRowCount =  data.getColumnValues().get(0).size();
            shiftColCount =  data.getRowValues().get(0).size();

            totalColumn = shiftColCount + data.getTotalColumns() + 1;
            totalRow = (shiftRowCount == 0 ? 1 : shiftRowCount) + data.getTotalRows();
        }
    }
    private CellStyle initCellStyle(Workbook wb, short dataType, short color) {

        var cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(dataType);
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class MergeData {

        int startRow = 0;
        int endRow = 0;
        int startCol = 0;
        int endCol = 0;
        String currentValue;

    }
}
