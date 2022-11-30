package ru.magnit.magreportbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.magnit.magreportbackend.dto.backup.BackupRequest;
import ru.magnit.magreportbackend.service.AdminService;
import ru.magnit.magreportbackend.util.LogHelper;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Администрирование приложения")
public class AdminController {

    public static final String ADMIN_LOGS = "/api/v1/admin/logs";
    public static final String OLAP_LOGS = "/api/v1/admin/olap-logs";
    public static final String CREATE_BACKUP = "/api/v1/admin/create-backup";
    public static final String LOAD_BACKUP = "/api/v1/admin/load-backup";

    private final AdminService adminService;

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение текущего основного файла лога")
    @PostMapping(value = ADMIN_LOGS,
            produces = APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    byte[]
    getActiveLog() {
        LogHelper.logInfoUserMethodStart();

        byte[] result = adminService.getMainActiveLog();

        LogHelper.logInfoUserMethodEnd();
        return result;
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение текущего файла лога Olap сервисов")
    @PostMapping(value = OLAP_LOGS,
            produces = APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getOlapActiveLog() {
        LogHelper.logInfoUserMethodStart();
        byte[] result = adminService.getOlapActiveLog();
        LogHelper.logInfoUserMethodEnd();
        return result;
    }


    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Создание бэкапа")
    @PostMapping(value = CREATE_BACKUP,
            produces = APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getBackupDB(
            @RequestBody BackupRequest request)  {
        LogHelper.logInfoUserMethodStart();
         var backup = adminService.createBackup(request);
        LogHelper.logInfoUserMethodEnd();

        return backup;
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Создание бэкапа")
    @PostMapping(value = LOAD_BACKUP,
            produces = APPLICATION_OCTET_STREAM_VALUE)
    public void loadBackupDB(
         //   @RequestParam("request") BackupUpRequest request,
            @RequestParam("file")
            MultipartFile uploadedFile)  {
        LogHelper.logInfoUserMethodStart();
        adminService.loadBackup(uploadedFile);
        LogHelper.logInfoUserMethodEnd();

    }
}
