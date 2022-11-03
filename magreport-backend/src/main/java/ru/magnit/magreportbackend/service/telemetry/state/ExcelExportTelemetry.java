package ru.magnit.magreportbackend.service.telemetry.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExcelExportTelemetry implements TelemetryState {
    INITIALIZING("Initializing"),
    SHEET_PREPARATION("Sheet preparation"),
    HEADER_WRITING("Header writing"),
    ROWS_WRITING("Rows writing"),
    FILE_WRITING("File writing"),
    APPLY_STYLES("Apply styles"),
    WORKBOOK_DISPOSAL("Workbook disposal");

    private final String description;
}
