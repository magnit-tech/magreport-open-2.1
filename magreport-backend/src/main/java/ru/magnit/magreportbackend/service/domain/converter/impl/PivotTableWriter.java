package ru.magnit.magreportbackend.service.domain.converter.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
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

    private Integer shiftRowCount = 0;
    private Integer shiftColCount = 0;
    private boolean mergeMode = false;
    private boolean columnsMetricPlacement = false;

    private boolean stylePivotTable = false;
    private Map<Long, String> mappingFields;
    private CellStyle cellStyle;

    private OlapCubeRequest cubeRequest;

    private int totalColumn;
    private List<String> rowMetaNames = Collections.emptyList();
    private List<String> columnMetaNames = Collections.emptyList();


    @Override
    public void convert(String templatePath) {

        final var telemetryId = telemetryService.init(ExcelExportTelemetry.INITIALIZING);
        telemetryService.setState(telemetryId, ExcelExportTelemetry.INITIALIZING);

        try (
                final var workbook = new SXSSFWorkbook(new XSSFWorkbook(new File(templatePath)), SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
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
            if (mergeMode) mergeCells(sheet);

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
            metaCell.setCellStyle(cellStyle);
            metaCell.setCellValue(columnMetaNames.get(i));


            for (int j = 0; j < data.getColumnValues().size(); j++) {
                var cell = row.createCell(j + shiftColCount + 1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data.getColumnValues().get(j).get(i));

            }
        }


        int startIndex = shiftRowCount - 1 == -1 ? 0 : shiftRowCount - 1;
        var metaRow = getRow(sheet, startIndex);


        for (int i = 0; i < rowMetaNames.size(); i++) {
            var cell = metaRow.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(rowMetaNames.get(i));
        }

        startIndex = shiftRowCount == 0 ? 1 : 0;
        for (int i = 0; i < data.getRowValues().size(); i++) {
            var row = getRow(sheet, startIndex + shiftRowCount);

            for (int j = 0; j < shiftColCount; j++) {
                var cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data.getRowValues().get(i).get(j));
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
            metaCell.setCellValue(getMetadataValue(aggregationType, fieldId));
            metaCell.setCellStyle(cellStyle);


            for (int j = 0; j < metricsValue.get(i).getR().size(); j++) {
                var cell = row.createCell(j + shiftColCount + 1);
                cell.setCellStyle(cellStyle);
                writeCellValue(cell, metricsValue.get(i).getR().get(j), type);
            }
        }
    }

    private void writeMeasuresColumnMetric(Sheet sheet) {

        int startIndexMetaParam = shiftColCount - 1 == -1 ? 0 : shiftColCount - 1;
        int startIndex = shiftColCount == 0 ? 1 : shiftColCount;
        for (int i = 0; i < shiftRowCount; i++) {
            var row = getRow(sheet, i);

            var metaCell = row.createCell(startIndexMetaParam);
            metaCell.setCellStyle(cellStyle);
            metaCell.setCellValue(columnMetaNames.get(i));

            int incrementMetricSize = 0;
            for (int j = 0; j < data.getColumnValues().size(); j++) {
                var cell = row.createCell(j + startIndex + incrementMetricSize);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data.getColumnValues().get(j).get(i));
                if (!data.getMetricValues().isEmpty())
                    incrementMetricSize += data.getMetricValues().size() - 1;
            }
        }


        var metaRow = getRow(sheet, shiftRowCount);

        for (int i = 0; i < rowMetaNames.size(); i++) {
            var cell = metaRow.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(rowMetaNames.get(i));
        }

        for (int i = 0; i < data.getRowValues().size(); i++) {
            var row = getRow(sheet, i + shiftRowCount + 1);

            for (int j = 0; j < shiftColCount; j++) {
                var cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data.getRowValues().get(i).get(j));
            }
        }
    }

    private void writeDataColumnMetric(Sheet sheet) {

        List<List<Pair<OlapMetricResponse, String>>> metricsValue = new ArrayList<>();

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
                    var metaCell = metaRow.createCell(shiftColCount + v);
                    metaCell.setCellValue(getMetadataValue(rowValues.get(v).getL().getAggregationType(), rowValues.get(v).getL().getFieldId()));
                    metaCell.setCellStyle(cellStyle);
                }

                var cell = row.createCell(v + shiftColCount);
                cell.setCellStyle(cellStyle);
                writeCellValue(cell, rowValues.get(v).getR(), rowValues.get(v).getL().getDataType());
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

    private void createCellStyles(Workbook wb) {

        cellStyle = wb.createCellStyle();

        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    private void initConfig(Workbook wb) {

        shiftRowCount = data.getColumnValues().isEmpty() ? 0 : data.getColumnValues().get(0).size();
        shiftColCount = data.getRowValues().isEmpty() ? 0 : data.getRowValues().get(0).size();

        createCellStyles(wb);

        mappingFields = metadata.fields().stream().collect(Collectors.toMap(ReportFieldMetadataResponse::id, ReportFieldMetadataResponse::name));
        cubeRequest = request.getCubeRequest();

        rowMetaNames = cubeRequest.getRowFields().stream().filter(mappingFields::containsKey).map(mappingFields::get).toList();
        columnMetaNames = cubeRequest.getColumnFields().stream().filter(mappingFields::containsKey).map(mappingFields::get).toList();

        if (config.containsKey("mergeMode")) mergeMode = (boolean) config.get("mergeMode");
        if (config.containsKey("columnsMetricPlacement"))
            columnsMetricPlacement = (boolean) config.get("columnsMetricPlacement");
        stylePivotTable = request.isStylePivotTable();

        if (columnsMetricPlacement && !data.getMetricValues().isEmpty())
            totalColumn = data.getTotalColumns() * data.getMetricValues().size() + shiftColCount;
        else totalColumn = data.getTotalColumns() + shiftColCount;
    }

    private void writeCellValue(Cell cell, String value, DataTypeEnum type) {

        value = value == null ? "" : value;

        if (!value.isEmpty()) {
            switch (type) {
                case INTEGER -> cell.setCellValue(Integer.parseInt(value));
                case STRING -> cell.setCellValue(value);
                case DOUBLE -> cell.setCellValue(Double.parseDouble(value));
                case DATE -> cell.setCellValue(LocalDate.parse(value));
                case TIMESTAMP -> cell.setCellValue(LocalDateTime.parse(value.replace(" ", "T")));
            }
        } else {
            cell.setCellValue(value);
        }
    }

    private void updateSizeColumns(SXSSFSheet sheet) {
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < totalColumn + 1; i++) sheet.autoSizeColumn(i, true);
    }

    private Row getRow(Sheet sheet, int index) {
        return sheet.getRow(index) == null ? sheet.createRow(index) : sheet.getRow(index);
    }

    private void mergeCells(Sheet sheet) {

        for (int i = 0; i < shiftRowCount; i++) {

            var row = sheet.getRow(i);


            String currentValue = null;
            int startIndex = 0;
            for (int cellIndex = 0; cellIndex <= totalColumn; cellIndex++) {
                var cell = row.getCell(cellIndex);
                if (cell != null) {

                    var newValue = cell.getStringCellValue();


                    if (currentValue == null) {
                        currentValue = newValue;
                        startIndex = cellIndex;
                        continue;
                    }

                    if ((currentValue.equals(newValue) || currentValue.equals("")) && cellIndex != totalColumn)
                        continue;

                    if (startIndex != cellIndex - 1)
                        sheet.addMergedRegion(new CellRangeAddress(i, i, startIndex, cellIndex == totalColumn ? cellIndex : cellIndex - 1));

                    currentValue = newValue;
                    startIndex = cellIndex;

                } else if (cellIndex == totalColumn - 1 && (startIndex != cellIndex - 1)) {
                    sheet.addMergedRegion(new CellRangeAddress(i, i, startIndex, cellIndex));

                }
            }
        }


        for (int cellIndex = 0; cellIndex < shiftColCount; cellIndex++) {

            String currentValue = null;
            int startIndex = 0;

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                var row = getRow(sheet, rowIndex);

                var cell = row.getCell(cellIndex);
                if (cell != null) {

                    var newValue = cell.getStringCellValue();

                    if (currentValue == null) {
                        currentValue = newValue;
                        startIndex = rowIndex;
                        continue;
                    }

                    if ((currentValue.equals(newValue) || currentValue.equals("")) && rowIndex != sheet.getLastRowNum())
                        continue;

                    if (startIndex != rowIndex - 1)
                        sheet.addMergedRegion(new CellRangeAddress(startIndex, rowIndex == sheet.getLastRowNum() ? rowIndex : rowIndex - 1, cellIndex, cellIndex));

                    currentValue = newValue;
                    startIndex = rowIndex;
                } else if (rowIndex == sheet.getLastRowNum() && (startIndex != rowIndex - 1)) {
                    sheet.addMergedRegion(new CellRangeAddress(startIndex, rowIndex, cellIndex, cellIndex));
                }

            }
        }


        sheet.getMergedRegions().forEach(r -> {
            RegionUtil.setBorderTop(BorderStyle.THIN, r, sheet);
            RegionUtil.setBorderBottom(BorderStyle.THIN, r, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, r, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, r, sheet);
        });

    }

    private SXSSFSheet getSheet(SXSSFWorkbook workbook) {
        var sheet = workbook.getSheet(nameDataList);

        if (sheet != null) {
            var indexSheet = workbook.getSheetIndex(sheet);
            workbook.removeSheetAt(indexSheet);
        }

        return workbook.createSheet(nameDataList);
    }

}



