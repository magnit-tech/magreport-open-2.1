package ru.magnit.magreportbackend.service.domain.converter.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.h2.util.Task;
import ru.magnit.magreportbackend.domain.dataset.DataTypeEnum;
import ru.magnit.magreportbackend.dto.inner.TaskInfo;
import ru.magnit.magreportbackend.dto.inner.jobengine.CacheRow;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportData;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.domain.converter.Reader;
import ru.magnit.magreportbackend.service.domain.converter.Writer;
import ru.magnit.magreportbackend.service.telemetry.TelemetryService;
import ru.magnit.magreportbackend.service.telemetry.state.ExcelExportTelemetry;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class ExcelWriter implements Writer {

    private final Reader reader;
    private final ReportData reportData;
    private final Path exportPath;
    private final String nameDataList;
    private final TelemetryService telemetryService;
    private final TaskInfo taskInfo;

    private final AtomicInteger rowCount = new AtomicInteger(0);
    private final AtomicInteger colCount = new AtomicInteger(0);

    @Override
    public void convert(String templatePath) {
        final var telemetryId = telemetryService.init(ExcelExportTelemetry.INITIALIZING, taskInfo);
        telemetryService.setState(telemetryId, ExcelExportTelemetry.INITIALIZING);
        try (
                final var inputStream = Files.newInputStream(Paths.get(templatePath));
                final var workbook = new SXSSFWorkbook(new XSSFWorkbook(inputStream), SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
                final var outputStream = new BufferedOutputStream(new FileOutputStream(exportPath.toFile()))
        ) {
            telemetryService.setState(telemetryId, ExcelExportTelemetry.SHEET_PREPARATION);
            var sheet =  workbook.getSheet(nameDataList);

            if( sheet != null){
                var indexSheet = workbook.getSheetIndex(sheet);
                workbook.removeSheetAt(indexSheet);
            }

            sheet = workbook.createSheet(nameDataList);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.HEADER_WRITING);
            writeHeader(workbook, sheet);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.ROWS_WRITING);
            writeRows(sheet);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.FILE_WRITING);
            workbook.write(outputStream);

            telemetryService.setState(telemetryId, ExcelExportTelemetry.WORKBOOK_DISPOSAL);
            // calling dispose() is a must - to not leave temp files behind
            workbook.dispose();

            telemetryService.logTimings(telemetryId);
            telemetryService.clear(telemetryId);
            log.debug("Number of rows: " + rowCount.get());
        } catch (Exception ex) {
            throw new ReportExportException("Error export report to excel file", ex);
        }
    }

    private void writeRows(SXSSFSheet sheet) {
        CacheRow cacheRow;
        final var numericCellStyle = sheet.getWorkbook().createCellStyle();
        numericCellStyle.setDataFormat((short) 4);
        final var integerCellStyle = sheet.getWorkbook().createCellStyle();
        integerCellStyle.setDataFormat((short) 1);

        while (null != (cacheRow = reader.getRow())) {
            var row = sheet.createRow(rowCount.getAndIncrement());
            var cellCounter = 0;
            for (var entry : cacheRow.entries()) {
                parseCell(numericCellStyle, integerCellStyle, row, cellCounter++, entry);
            }
        }
    }

    private void parseCell(CellStyle numericCellStyle, CellStyle integerCellStyle, org.apache.poi.xssf.streaming.SXSSFRow row, int cellCounter, ru.magnit.magreportbackend.dto.inner.jobengine.CacheEntry entry) {
        Cell cell = row.createCell(cellCounter);
        if (entry.fieldData().dataType() == DataTypeEnum.INTEGER) cell.setCellStyle(integerCellStyle);
        if (entry.fieldData().dataType() == DataTypeEnum.DOUBLE) cell.setCellStyle(numericCellStyle);
        if ((entry.fieldData().dataType() == DataTypeEnum.INTEGER || entry.fieldData().dataType() == DataTypeEnum.DOUBLE)) {
            var doubleValue = tryParseDouble(entry.value());
            if (doubleValue == null) {
                cell.setCellValue(entry.value());
            } else {
                cell.setCellValue(doubleValue);
            }
        } else {
            cell.setCellValue(entry.value());
        }
    }

    private Double tryParseDouble(String number) {
        if (number == null || number.isBlank()) return null;

        Double result;
        try {
            result = Double.parseDouble(number);
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }

    private void writeHeader(SXSSFWorkbook workbook, SXSSFSheet sheet) {
        var row = sheet.createRow(rowCount.getAndIncrement());

        // Create font
        var font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLUE.getIndex());

        // Create style
        var style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        // Freeze header string
        sheet.createFreezePane(0, 1);

        var names = new ArrayList<Name>(workbook.getAllNames());
        names.forEach(workbook::removeName);

        // Populate header cells with aforementioned style (style applicable only for cells)
        reportData.getVisibleFields().forEach(field -> {
                    final var cell = row.createCell(colCount.getAndIncrement());
                    cell.setCellValue(field.name());
                    cell.setCellStyle(style);

                    final var name = workbook.createName();
                    name.setNameName("Field" + colCount.get());
                    name.setComment(field.columnName());

                    final var cellAddress = "'" + sheet.getSheetName() + "'!$" + cell.getAddress().formatAsString().replace("1", "") + "$1";
                    name.setRefersToFormula(cellAddress);
                }
        );
    }
}
