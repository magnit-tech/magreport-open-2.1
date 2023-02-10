package ru.magnit.magreportbackend.service.domain.converter.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.domain.olap.AggregationType;
import ru.magnit.magreportbackend.dto.request.olap.OlapCubeRequest;
import ru.magnit.magreportbackend.dto.request.olap.OlapExportPivotTableRequest;
import ru.magnit.magreportbackend.dto.response.olap.OlapCubeResponse;
import ru.magnit.magreportbackend.dto.response.olap.OlapMetricResponse;
import ru.magnit.magreportbackend.dto.response.report.ReportFieldMetadataResponse;
import ru.magnit.magreportbackend.dto.response.reportjob.ReportJobMetadataResponse;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.domain.converter.Writer;
import ru.magnit.magreportbackend.service.telemetry.TelemetryService;
import ru.magnit.magreportbackend.service.telemetry.state.ExcelExportTelemetry;
import ru.magnit.magreportbackend.util.Pair;

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
    private List<String> rowMetaNames = Collections.emptyList();
    private List<String> columnMetaNames = Collections.emptyList();

    private static final String ERROR_COLOR_TEXT = "Unknown color excel pivot table:";


    @Override
    public void convert(String templatePath) {

        final var telemetryId = telemetryService.init(ExcelExportTelemetry.INITIALIZING);
        telemetryService.setState(telemetryId, ExcelExportTelemetry.INITIALIZING);

        try (
                final var inputStream = Files.newInputStream(Paths.get(templatePath));
                final var workbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream), 150000);
                final var outputStream = new BufferedOutputStream(new FileOutputStream(exportPath.toFile()))
        ) {
            initConfig(workbook);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.SHEET_PREPARATION);
            var sheet = getSheet(workbook);

            if (columnsMetricPlacement) {
                telemetryService.setState(telemetryId, ExcelExportTelemetry.HEADER_WRITING);
                writeMeasuresColumnMetric(sheet);

                telemetryService.setState(telemetryId, ExcelExportTelemetry.ROWS_WRITING);
                writeDataColumnMetric(sheet);
            } else {
                telemetryService.setState(telemetryId, ExcelExportTelemetry.HEADER_WRITING);
                writeMeasuresRowMetric(sheet);

                telemetryService.setState(telemetryId, ExcelExportTelemetry.ROWS_WRITING);
                writeDataRowMetric(sheet);
            }

            telemetryService.setState(telemetryId, ExcelExportTelemetry.APPLY_STYLES);
            starterMergeCell(sheet);

            updateSizeColumns(sheet);

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

    private void writeMeasuresRowMetric(Sheet sheet) {

        for (int i = 0; i < shiftRowCount; i++) {
            var row = getRow(sheet, i);

            var metaCell = row.createCell(shiftColCount);
            writeCellValue(metaCell, columnMetaNames.get(i), DataTypeEnum.STRING, 1);

            var type = DataTypeEnum.valueOf(typeFields.get(new ArrayList<>(request.getCubeRequest().getColumnFields()).get(i)));

            for (int j = 0; j < data.getColumnValues().size(); j++) {
                var cell = row.createCell(j + shiftColCount + 1);
                writeCellValue(cell, data.getColumnValues().get(j).get(i), type, 2);
            }
        }


        int startIndex = shiftRowCount - 1 == -1 ? 0 : shiftRowCount - 1;
        var metaRow = getRow(sheet, startIndex);


        for (int i = 0; i < rowMetaNames.size(); i++) {
            var cell = metaRow.createCell(i);
            writeCellValue(cell, rowMetaNames.get(i), DataTypeEnum.STRING, 1);
        }

        startIndex = shiftRowCount == 0 ? 1 : 0;
        var types = request.getCubeRequest().getRowFields().stream().map(f -> typeFields.get(f)).map(DataTypeEnum::valueOf).toList();
        for (int i = 0; i < data.getRowValues().size(); i++) {
            var row = getRow(sheet, startIndex + shiftRowCount);

            for (int j = 0; j < shiftColCount; j++) {

                var cell = row.createCell(j);
                writeCellValue(cell, data.getRowValues().get(i).get(j), types.get(j), 2);
            }

            if (!data.getMetricValues().isEmpty())
                startIndex += data.getMetricValues().size();
            else
                startIndex++;
        }
    }

    private void writeDataRowMetric(Sheet sheet) {

        List<Pair<OlapMetricResponse, List<String>>> metricsValue = new ArrayList<>();

        for (int r = 0; r < data.getTotalRows(); r++) {
            for (var metric : data.getMetricValues()) {
                var valuesRow = new ArrayList<String>();
                for (var i : metric.getValues())
                    valuesRow.add(i.get(r));
                metricsValue.add(new Pair<>(metric, valuesRow));
            }
        }
        int startIndex = shiftRowCount == 0 ? 1 : shiftRowCount;
        for (int i = 0; i < metricsValue.size(); i++) {

            var type = metricsValue.get(i).getL().getDataType();
            var fieldId = metricsValue.get(i).getL().getFieldId();
            var aggregationType = metricsValue.get(i).getL().getAggregationType();

            var row = getRow(sheet, i + startIndex);
            var metaCell = row.createCell(shiftColCount);
            writeCellValue(metaCell, getMetadataValue(aggregationType, fieldId), DataTypeEnum.STRING, 1);


            for (int j = 0; j < metricsValue.get(i).getR().size(); j++) {
                var cell = row.createCell(j + shiftColCount + 1);
                writeCellValue(cell, metricsValue.get(i).getR().get(j), type, 3);

            }
        }
    }

    private void writeMeasuresColumnMetric(Sheet sheet) {

        int startIndexMetaParam = shiftColCount - 1 == -1 ? 0 : shiftColCount - 1;
        int startIndex = shiftColCount == 0 ? 1 : shiftColCount;
        for (int i = 0; i < shiftRowCount; i++) {
            var row = getRow(sheet, i);

            var metaCell = row.createCell(startIndexMetaParam);
            writeCellValue(metaCell, columnMetaNames.get(i), DataTypeEnum.STRING, 1);


            int incrementMetricSize = 0;
            var type = DataTypeEnum.valueOf(typeFields.get(new ArrayList<>(request.getCubeRequest().getColumnFields()).get(i)));
            for (int j = 0; j < data.getColumnValues().size(); j++) {
                var cell = row.createCell(j + startIndex + incrementMetricSize);
                writeCellValue(cell, data.getColumnValues().get(j).get(i), type, 2);
                if (!data.getMetricValues().isEmpty())
                    incrementMetricSize += data.getMetricValues().size() - 1;
            }
        }


        var metaRow = getRow(sheet, shiftRowCount);

        for (int i = 0; i < rowMetaNames.size(); i++) {
            var cell = metaRow.createCell(i);
            writeCellValue(cell, rowMetaNames.get(i), DataTypeEnum.STRING, 1);
        }

        var types = request.getCubeRequest().getRowFields().stream().map(f -> typeFields.get(f)).map(DataTypeEnum::valueOf).toList();
        for (int i = 0; i < data.getRowValues().size(); i++) {
            var row = getRow(sheet, i + shiftRowCount + 1);

            for (int j = 0; j < shiftColCount; j++) {
                var cell = row.createCell(j);
                writeCellValue(cell, data.getRowValues().get(i).get(j), types.get(j), 2);
            }
        }
    }

    private void writeDataColumnMetric(Sheet sheet) {

        List<List<Pair<OlapMetricResponse, String>>> metricsValue = new ArrayList<>();

        var shift = shiftColCount == 0 ? 1 : shiftColCount;

        for (int r = 0; r < data.getTotalRows(); r++) {
            var rowValues = new ArrayList<Pair<OlapMetricResponse, String>>();
            for (int c = 0; c < data.getTotalColumns(); c++)
                for (int m = 0; m < data.getMetricValues().size(); m++) {
                    rowValues.add(new Pair<>(data.getMetricValues().get(m), data.getMetricValues().get(m).getValues().get(c).get(r)));
                }
            metricsValue.add(rowValues);
        }
        var metaRow = getRow(sheet, shiftRowCount);
        for (int i = 0; i < metricsValue.size(); i++) {

            var rowValues = metricsValue.get(i);
            var row = getRow(sheet, i + shiftRowCount + 1);

            for (int v = 0; v < rowValues.size(); v++) {
                if (i == 0) {
                    var metaCell = metaRow.createCell(shift + v);
                    writeCellValue(metaCell, getMetadataValue(rowValues.get(v).getL().getAggregationType(), rowValues.get(v).getL().getFieldId()), DataTypeEnum.STRING, 1);
                }

                var cell = row.createCell(v + shift);
                writeCellValue(cell, rowValues.get(v).getR(), rowValues.get(v).getL().getDataType(), 3);
            }
        }
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


    private void initConfig(Workbook wb) {

        shiftRowCount = data.getColumnValues().isEmpty() ? 0 : data.getColumnValues().get(0).size();
        shiftColCount = data.getRowValues().isEmpty() ? 0 : data.getRowValues().get(0).size();

        initCellStyles(wb);

        mappingFields = metadata.fields().stream().collect(Collectors.toMap(ReportFieldMetadataResponse::id, ReportFieldMetadataResponse::name));
        typeFields = metadata.fields().stream().collect(Collectors.toMap(ReportFieldMetadataResponse::id, ReportFieldMetadataResponse::type));

        OlapCubeRequest cubeRequest = request.getCubeRequest();

        rowMetaNames = cubeRequest.getRowFields().stream().filter(mappingFields::containsKey).map(mappingFields::get).toList();
        columnMetaNames = cubeRequest.getColumnFields().stream().filter(mappingFields::containsKey).map(mappingFields::get).toList();

        if (config.containsKey("mergeMode")) mergeMode = (boolean) config.get("mergeMode");
        if (config.containsKey("columnsMetricPlacement"))
            columnsMetricPlacement = (boolean) config.get("columnsMetricPlacement");

        if (columnsMetricPlacement && !data.getMetricValues().isEmpty())
            totalColumn = data.getTotalColumns() * data.getMetricValues().size() + shiftColCount;
        else totalColumn = data.getTotalColumns() + shiftColCount;
    }

    private void writeCellValue(Cell cell, String value, DataTypeEnum type, int colorType) {

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

    private void setStyleCell(Cell cell, CellStyle metaStyle, CellStyle measureStyle, CellStyle metricStyle, int colorType) {
        switch (colorType) {
            case 1 -> cell.setCellStyle(metaStyle);
            case 2 -> cell.setCellStyle(measureStyle);
            case 3 -> cell.setCellStyle(metricStyle);
            default -> throw new IllegalStateException(String.format("%s%s", ERROR_COLOR_TEXT, colorType));
        }
    }

    private void updateSizeColumns(SXSSFSheet sheet) {
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < totalColumn + 1; i++) sheet.autoSizeColumn(i, true);
    }

    private Row getRow(Sheet sheet, int index) {
        return sheet.getRow(index) == null ? sheet.createRow(index) : sheet.getRow(index);
    }

    private void starterMergeCell(Sheet sheet) {

        if (shiftColCount == 0)
            mergeRowCell(sheet, shiftRowCount, shiftColCount + 1, totalColumn + 1);
        else
            mergeRowCell(sheet, shiftRowCount, shiftColCount, totalColumn);

        if (columnsMetricPlacement)
            mergeColumnCell(sheet, shiftRowCount + 1, sheet.getLastRowNum() + 1, shiftColCount);
        else
            mergeColumnCell(sheet, shiftRowCount, sheet.getLastRowNum() + 1, shiftColCount);

        sheet.getMergedRegions().forEach(r -> {
            RegionUtil.setBorderTop(BorderStyle.THIN, r, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, r, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, r, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, r, sheet);
        });

    }

    private void mergeRowCell(Sheet sheet, int stopMergeRow, int startMergeColumn, int stopMergeColumn) {

        for (int i = 0; i < stopMergeRow; i++) {
            var row = sheet.getRow(i);
            String currentValue = null;
            int startIndex = 0;

            for (int cellIndex = startMergeColumn; cellIndex < stopMergeColumn; cellIndex++) {
                var cell = row.getCell(cellIndex);
                if (cell != null) {

                    var newValue = getStringValueCell(cell);

                    if (currentValue == null) {
                        currentValue = newValue;
                        startIndex = cellIndex;
                        continue;
                    }

                    if (((currentValue.equals(newValue) && mergeMode) || newValue.equals("")) && cellIndex != stopMergeColumn - 1)
                        continue;

                    if (startIndex != cellIndex - 1)
                        sheet.addMergedRegion(new CellRangeAddress(i, i, startIndex, cellIndex != stopMergeColumn - 1 ? cellIndex - 1 : cellIndex));

                    currentValue = newValue;
                    startIndex = cellIndex;

                } else if (cellIndex == stopMergeColumn - 1 && (startIndex != cellIndex))
                    sheet.addMergedRegion(new CellRangeAddress(i, i, startIndex, cellIndex));

            }
        }


    }

    private void mergeColumnCell(Sheet sheet, int startMergeRow, int stopMergeRow, int stopMergeColumn) {

        for (int cellIndex = 0; cellIndex < stopMergeColumn; cellIndex++) {

            String currentValue = null;
            int startIndex = 0;

            for (int rowIndex = startMergeRow; rowIndex < stopMergeRow; rowIndex++) {
                var row = getRow(sheet, rowIndex);

                var cell = row.getCell(cellIndex);
                if (cell != null) {

                    var newValue = getStringValueCell(cell);

                    if (newValue.isEmpty() && columnsMetricPlacement) continue;

                    if (currentValue == null) {
                        currentValue = newValue;
                        startIndex = rowIndex;
                        continue;
                    }

                    if (((currentValue.equals(newValue) && mergeMode) || newValue.equals("")) && rowIndex != stopMergeRow - 1)
                        continue;

                    if (startIndex != rowIndex - 1)
                        sheet.addMergedRegion(new CellRangeAddress(startIndex, rowIndex != stopMergeRow - 1 ? rowIndex - 1 : rowIndex, cellIndex, cellIndex));

                    currentValue = newValue;
                    startIndex = rowIndex;
                } else if (rowIndex == stopMergeRow - 1 && (startIndex != rowIndex)) {
                    sheet.addMergedRegion(new CellRangeAddress(startIndex, rowIndex, cellIndex, cellIndex));
                }

            }
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

    private String getStringValueCell(Cell cell) {
        return switch (cell.getCellType()) {
            case _NONE, BLANK -> "";
            case NUMERIC -> cell.getNumericCellValue() + "";
            case STRING -> cell.getStringCellValue() + "";
            case FORMULA -> cell.getCellFormula();
            case BOOLEAN -> cell.getBooleanCellValue() + "";
            case ERROR -> cell.getErrorCellValue() + "";
        };
    }
}



