package ru.magnit.magreportbackend.service.domain.converter.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.olap.AggregationType;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldMetadataResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobMetadataResponse;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.domain.converter.Writer;
import ru.magnit.magreportbackend.service.telemetry.TelemetryService;
import ru.magnit.magreportbackend.service.telemetry.state.ExcelExportTelemetry;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PivotTableWriter implements Writer {


    private final OlapCubeResponse cubeData;
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
        final var telemetryId = telemetryService.init(ExcelExportTelemetry.INITIALIZING);
        telemetryService.setState(telemetryId, ExcelExportTelemetry.INITIALIZING);

        try (
                final var inputStream = Files.newInputStream(Paths.get(templatePath));
                final var workbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream), SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
                final var outputStream = new BufferedOutputStream(new FileOutputStream(exportPath.toFile()))
        ) {

            initConfig(workbook);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.SHEET_PREPARATION);
            var sheet = getSheet(workbook);

            var mergeCol = getMergeObjects(sheet, shiftColCount, false);
            var mergeRow = getMergeObjects(sheet, shiftRowCount, true);

            var typesRow = request.getCubeRequest().getRowFields().stream().map(f -> typeFields.get(f)).map(DataTypeEnum::valueOf).toList();
            var typesCol = request.getCubeRequest().getColumnFields().stream().map(f -> typeFields.get(f)).map(DataTypeEnum::valueOf).toList();

            telemetryService.setState(telemetryId, ExcelExportTelemetry.ROWS_WRITING);

            if (columnsMetricPlacement)
                writeColumnMetricPlacementTable(sheet, mergeRow, mergeCol, typesRow, typesCol);
            else
                writeRowMetricPlacementTable(sheet, mergeRow, mergeCol, typesRow, typesCol);


            telemetryService.setState(telemetryId, ExcelExportTelemetry.APPLY_STYLES);

            updateSizeColumns(sheet);

            for (var merge : mergeRow) {
                if (merge.startCol != totalColumn - 1 && merge.checkMerge(merge.endRow, totalColumn - 1))
                    sheet.addMergedRegion(new CellRangeAddress(merge.startRow, merge.endRow, merge.startCol, totalColumn - 1));
            }

            for (var merge : mergeCol) {
                if (merge.startRow != totalRow - 1 && merge.checkMerge(totalRow - 1, merge.endCol))
                    sheet.addMergedRegion(new CellRangeAddress(merge.startRow, totalRow - 1, merge.startCol, merge.endCol));
            }

            telemetryService.setState(telemetryId, ExcelExportTelemetry.FILE_WRITING);
            workbook.write(outputStream);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.WORKBOOK_DISPOSAL);
            workbook.dispose();

            telemetryService.logTimings(telemetryId);
            telemetryService.clear(telemetryId);

        } catch (Exception ex) {
            throw new ReportExportException("Error export report to excel file", ex);
        }
    }


    private void writeColumnMetricPlacementTable(Sheet sheet, List<MergeData> mergeRow, List<MergeData> mergeCol, List<DataTypeEnum> typesRow, List<DataTypeEnum> typesCol) {

        int startIndexMetaParam = shiftColCount - 1 == -1 ? 0 : shiftColCount - 1;
        var shift = shiftColCount == 0 ? 1 : shiftColCount;

        int indexRow = 0;
        while (indexRow < totalRow) {
            var row = sheet.createRow(indexRow);
            int index = 0;
            int indexMetric = 0;
            int indexValueMetric = 0;
            int indexMetaValueMetric = 0;

            int indexCell = 0;
            while(indexCell < totalColumn){
                row.createCell(indexCell).setCellStyle(textCellMetricStyle);
                indexCell++;
            }

            for (int indexCol = 0; indexCol < totalColumn; indexCol++) {
                var cell = row.getCell(indexCol);

                if (indexRow < shiftRowCount) {
                    if (indexCol == startIndexMetaParam) {
                        writeCellValue(cell, columnMetaNames.get(indexRow), DataTypeEnum.STRING, ColorCell.META);
                    }

                    if (indexCol >= shift ) {

                        var mergeCell = mergeRow.get(indexRow);
                        var type = typesCol.get(indexRow);
                        var value = cubeData.getColumnValues().get(index).get(indexRow);
                        writeCellValue(cell, value, type, ColorCell.MEASURE);
                        mergeCell.mergeCell(value, cell.getRowIndex(), cell.getColumnIndex());

                        indexCol += cubeData.getMetricValues().size() - 1;
                        index++;
                    }

                } else {

                    if (indexRow == shiftRowCount) {
                        if (indexCol >= shift) {
                            var metric = cubeData.getMetricValues().get(indexMetaValueMetric);
                            writeCellValue(
                                    cell,
                                    getMetadataValue(metric.getAggregationType(), metric.getFieldId()),
                                    DataTypeEnum.STRING,
                                    ColorCell.META);

                            if (indexMetaValueMetric == cubeData.getMetricValues().size() - 1) {
                                indexMetaValueMetric = 0;
                            } else indexMetaValueMetric++;
                        } else
                            writeCellValue(
                                    cell,
                                    rowMetaNames.isEmpty() ? "" : rowMetaNames.get(indexCol),
                                    DataTypeEnum.STRING,
                                    ColorCell.META);

                    } else {

                        if (indexCol >= shift) {

                            writeCellValue(
                                    cell,
                                    cubeData.getMetricValues().get(indexMetric).getValues().get(indexValueMetric).get(indexRow - shiftRowCount - 1),
                                    cubeData.getMetricValues().get(indexMetric).getDataType(),
                                    ColorCell.METRIC);

                            if (indexMetric == cubeData.getMetricValues().size() - 1) {
                                indexMetric = 0;
                                indexValueMetric++;
                            } else
                                indexMetric++;
                        } else {
                            var mergeCell = mergeCol.get(indexCol);
                            var value = cubeData.getRowValues().get(indexRow - shiftRowCount - 1).isEmpty() ? "" : cubeData.getRowValues().get(indexRow - shiftRowCount - 1).get(indexCol);
                            writeCellValue(
                                    cell,
                                    value,
                                    typesRow.get(indexCol),
                                    ColorCell.MEASURE);
                            mergeCell.mergeCell(value, cell.getRowIndex(), cell.getColumnIndex());
                        }
                    }
                }
            }
            indexRow++;
        }
    }

    private void writeRowMetricPlacementTable(Sheet sheet, List<MergeData> mergeRow, List<MergeData> mergeCol, List<DataTypeEnum> typesRow, List<DataTypeEnum> typesCol) {

        int indexMetric = 0;
        int indexValueMetric = 0;
        int indexPrevRow = -1;

        int indexRow = 0;
        while (indexRow < totalRow) {
            var row = sheet.createRow(indexRow);


            for (int indexCol = 0; indexCol < totalColumn; indexCol++) {
                var cell = row.createCell(indexCol);
                cell.setCellStyle(textCellMetricStyle);
                if (indexRow < shiftRowCount) {

                    if (indexRow == shiftRowCount - 1 && indexCol < shiftColCount) {
                        writeCellValue(
                                cell,
                                rowMetaNames.isEmpty() ? "" : rowMetaNames.get(indexCol),
                                DataTypeEnum.STRING,
                                ColorCell.META);
                    }

                    if (indexCol == shiftColCount) {
                        writeCellValue(cell, columnMetaNames.get(indexRow), DataTypeEnum.STRING, ColorCell.META);
                    }

                    if (indexCol > shiftColCount) {
                        var mergeCell = mergeRow.get(indexRow);
                        var type = typesCol.get(indexRow);
                        var value = cubeData.getColumnValues().get(indexCol - shiftColCount - 1).get(indexRow);
                        writeCellValue(cell, value, type, ColorCell.MEASURE);
                        mergeCell.mergeCell(value, cell.getRowIndex(), cell.getColumnIndex());
                    }
                } else {

                    if (indexCol < shiftColCount && indexPrevRow != indexValueMetric) {

                        var mergeCell = mergeCol.get(indexCol);
                        var value = cubeData.getRowValues().get(indexValueMetric).isEmpty() ? "" : cubeData.getRowValues().get(indexValueMetric).get(indexCol);
                        writeCellValue(
                                cell,
                                value,
                                typesRow.get(indexCol),
                                ColorCell.MEASURE);
                        mergeCell.mergeCell(value, cell.getRowIndex(), cell.getColumnIndex());
                    }

                    if (indexCol == shiftColCount) {
                        var metric = cubeData.getMetricValues().get(indexMetric);
                        writeCellValue(
                                cell,
                                getMetadataValue(metric.getAggregationType(), metric.getFieldId()),
                                DataTypeEnum.STRING,
                                ColorCell.META);
                    }

                    if (indexCol > shiftColCount) {

                        writeCellValue(
                                cell,
                                cubeData.getMetricValues().get(indexMetric).getValues().get(indexCol - shiftColCount - 1).get(indexValueMetric),
                                cubeData.getMetricValues().get(indexMetric).getDataType(),
                                ColorCell.METRIC);
                    }
                }
            }

            if (indexRow >= shiftRowCount) {
                indexPrevRow = indexValueMetric;
                if (indexMetric == cubeData.getMetricValues().size() - 1) {
                    indexMetric = 0;
                    indexValueMetric++;

                } else
                    indexMetric++;
            }
            indexRow++;
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

    private void initValues() {

        if (columnsMetricPlacement) {

            shiftRowCount = cubeData.getColumnValues().get(0).size();
            shiftColCount = cubeData.getRowValues().get(0).isEmpty() ? 1 : cubeData.getRowValues().get(0).size();

            totalColumn = shiftColCount + cubeData.getTotalColumns() * cubeData.getMetricValues().size();
            totalRow = shiftRowCount + cubeData.getTotalRows() + 1;
        } else {
            shiftRowCount = cubeData.getColumnValues().get(0).isEmpty() ? 1 : cubeData.getColumnValues().get(0).size();
            shiftColCount = cubeData.getRowValues().get(0).size();

            totalColumn = shiftColCount + cubeData.getTotalColumns() + 1;
            totalRow = shiftRowCount + cubeData.getTotalRows() * cubeData.getMetricValues().size();
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

    private List<MergeData> getMergeObjects(Sheet sheet, int countObjects, boolean rowMerge) {

        var objects = new ArrayList<MergeData>();

        for (int i = 0; i < countObjects; i++) {
            var mergeObject = new MergeData(sheet, rowMerge);
            objects.add(mergeObject);
        }
        return objects;
    }


    private String getMetadataValue(AggregationType type, Long fieldId) {

        var nameField = mappingFields.get(fieldId);

        var text = switch (type) {
            case COUNT -> "Количество";
            case COUNT_DISTINCT -> "Кол-во уникальных";
            case SUM -> "Сумма";
            case MAX -> "Макс.";
            case MIN -> "Мин.";
            case AVG -> "Средн.";
        };

        return String.format("%s %s", text, nameField);

    }

    private void updateSizeColumns(SXSSFSheet sheet) {
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < totalColumn + 1; i++) sheet.autoSizeColumn(i, true);
    }

    private void writeCellValue(Cell cell, String value, DataTypeEnum type, ColorCell colorType) {

        value = value == null ? "" : value;

        if (!value.isEmpty()) {
            switch (type) {
                case INTEGER -> {
                    setStyleCell(cell, integerCellMetaStyle, integerCellMeasureStyle, integerCellMetricStyle, colorType);
                    cell.setCellValue(Integer.parseInt(value));
                }
                case STRING, BOOLEAN -> {
                    setStyleCell(cell, textCellMetaStyle, textCellMeasureStyle, textCellMetricStyle, colorType);
                    cell.setCellValue(value);
                }
                case DOUBLE -> {
                    setStyleCell(cell, numericCellMetaStyle, numericCellMeasureStyle, numericCellMetricStyle, colorType);
                    cell.setCellValue(Double.parseDouble(value));
                }
                case DATE -> {
                    setStyleCell(cell, dateCellMetaStyle, dateCellMeasureStyle, dateCellMetricStyle, colorType);
                    cell.setCellValue(LocalDate.parse(value));
                }
                case TIMESTAMP -> {
                    setStyleCell(cell, dateCellMetaStyle, dateCellMeasureStyle, dateCellMetricStyle, colorType);
                    cell.setCellValue(LocalDateTime.parse(value.replace(" ", "T")));
                }
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }
        } else {
            setStyleCell(cell, textCellMetaStyle, textCellMeasureStyle, textCellMetricStyle, colorType);
            cell.setCellValue(value);
        }

    }

    private void setStyleCell(Cell cell, CellStyle metaStyle, CellStyle measureStyle, CellStyle metricStyle, ColorCell color) {
        switch (color) {
            case META -> cell.setCellStyle(metaStyle);
            case MEASURE -> cell.setCellStyle(measureStyle);
            case METRIC -> cell.setCellStyle(metricStyle);
            default -> throw new IllegalStateException(String.format("%s%s", ERROR_COLOR_TEXT, color));
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private class MergeData {

        int startRow;
        int endRow;
        int startCol;
        int endCol;
        String currentValue;
        Sheet sheet;
        boolean rowMerge;

        public MergeData(Sheet sheet, boolean rowMerge) {
            this.sheet = sheet;
            this.rowMerge = rowMerge;
        }

        void mergeCell(String value, int row, int col) {
            if (currentValue == null) {
                currentValue = value;
                startRow = endRow = row;
                startCol = endCol = col;

            } else {

                if (currentValue.equals(value) && mergeMode && checkMerge(row, col)) {
                    endRow = row;
                    endCol = col;
                    return;
                }

                endRow = rowMerge ? row : row - 1;
                endCol = rowMerge ? col - 1 : col;

                if (startRow != endRow || startCol != endCol) {
                    sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, startCol, endCol));
                }

                startRow = row;
                startCol = col;
            }
        }

        public boolean checkMerge(int endRow, int endCol) {

            boolean checkMerge;
            if (rowMerge)
                checkMerge = sheet.getMergedRegions().stream().anyMatch(c -> c.containsRow(startRow - 1) && c.containsRow(endRow - 1) && c.containsColumn(startCol) && c.containsColumn(endCol)) || startRow == 0 || (endCol - startCol) == (cubeData.getMetricValues().size() - 1);
            else
                checkMerge = sheet.getMergedRegions().stream().anyMatch(c -> c.containsRow(startRow) && c.containsRow(endRow) && c.containsColumn(startCol - 1) && c.containsColumn(endCol - 1)) || startCol == 0 || (endRow - startRow) == (cubeData.getMetricValues().size() - 1);

            return checkMerge;

        }

    }

    private enum ColorCell {
        META,
        MEASURE,
        METRIC
    }
}
