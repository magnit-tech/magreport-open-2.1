package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.folder.FolderRequest;
import ru.magnit.magreportbackend.dto.request.folder.PermissionCheckRequest;
import ru.magnit.magreportbackend.dto.request.folderreport.FolderPermissionSetRequest;
import ru.magnit.magreportbackend.dto.request.folderreport.RoleAddPermissionRequest;
import ru.magnit.magreportbackend.dto.response.folder.FolderNodeResponse;
import ru.magnit.magreportbackend.dto.response.permission.DataSetFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.DataSourceFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.ExcelTemplateFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FilterInstanceFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FilterTemplateFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionCheckResponse;
import ru.magnit.magreportbackend.dto.response.permission.FolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.ReportFolderPermissionsResponse;
import ru.magnit.magreportbackend.dto.response.permission.RolePermissionResponse;
import ru.magnit.magreportbackend.dto.response.permission.SecurityFilterFolderPermissionsResponse;
import ru.magnit.magreportbackend.service.domain.DataSetDomainService;
import ru.magnit.magreportbackend.service.domain.DataSourceDomainService;
import ru.magnit.magreportbackend.service.domain.ExcelTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FilterInstanceDomainService;
import ru.magnit.magreportbackend.service.domain.FilterTemplateDomainService;
import ru.magnit.magreportbackend.service.domain.FolderDomainService;
import ru.magnit.magreportbackend.service.domain.FolderPermissionsDomainService;
import ru.magnit.magreportbackend.service.domain.ReportDomainService;
import ru.magnit.magreportbackend.service.domain.SecurityFilterDomainService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderPermissionsService {

    private final FolderPermissionsDomainService service;

    private final FolderDomainService folderDomainService;
    private final ReportDomainService reportDomainService;
    private final DataSourceDomainService dataSourceDomainService;
    private final DataSetDomainService dataSetDomainService;
    private final ExcelTemplateDomainService excelTemplateDomainService;
    private final FilterInstanceDomainService filterInstanceDomainService;
    private final FilterTemplateDomainService filterTemplateDomainService;
    private final SecurityFilterDomainService securityFilterDomainService;



    public FolderPermissionsResponse getFolderReportPermissions(FolderRequest request) {
        return service.getFolderReportPermissions(request.getId());
    }

    public FolderPermissionsResponse setFolderReportPermissions(FolderPermissionSetRequest request) {

        var currentFolderRolePermission = service.getFolderReportPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getFolderReportBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(folderDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateFolderReportPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addFolderReportPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });

        return service.getFolderReportPermissions(request.getFolderId());
    }

    public ReportFolderPermissionsResponse getReportFolderPermissions(FolderRequest request) {
        return service.getReportFolderPermissions(request.getId());
    }

    public ReportFolderPermissionsResponse setReportFolderPermissions(FolderPermissionSetRequest request) {

        var currentFolderRolePermission = service.getReportFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getReportFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(reportDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateReportFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteReportFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteReportFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addReportFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });

        return service.getReportFolderPermissions(request.getFolderId());
    }

    public DataSourceFolderPermissionsResponse getDataSourceFolderPermissions(FolderRequest request) {
        return service.getDataSourceFolderPermissions(request.getId());
    }

    public DataSourceFolderPermissionsResponse setDataSourceFolderPermissions(FolderPermissionSetRequest request) {
        var currentFolderRolePermission = service.getDataSourceFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getDataSourceFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(dataSourceDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateDataSourceFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteDataSourceFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteDataSourceFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addDataSourceFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });

        return service.getDataSourceFolderPermissions(request.getFolderId());
    }

    public DataSetFolderPermissionsResponse getDataSetFolderPermissions(FolderRequest request) {
        return service.getDataSetFolderPermissions(request.getId());
    }

    public DataSetFolderPermissionsResponse setDataSetFolderPermissions(FolderPermissionSetRequest request) {
        var currentFolderRolePermission = service.getDataSetFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getDataSetFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(dataSetDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateDataSetFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteDataSetFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteDataSetFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addDataSetFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });


        return service.getDataSetFolderPermissions(request.getFolderId());
    }

    public ExcelTemplateFolderPermissionsResponse getExcelTemplateFolderPermissions(FolderRequest request) {

        return service.getExcelTemplateFolderPermissions(request.getId());
    }

    public ExcelTemplateFolderPermissionsResponse setExcelTemplateFolderPermissions(FolderPermissionSetRequest request) {
        var currentFolderRolePermission = service.getExcelTemplateFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getExcelTemplateFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(excelTemplateDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateExcelTemplateFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteExcelTemplateFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteExcelTemplateFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addExcelTemplateFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });

        return service.getExcelTemplateFolderPermissions(request.getFolderId());
    }

    public FilterInstanceFolderPermissionsResponse getFilterInstanceFolderPermissions(FolderRequest request) {
        return service.getFilterInstanceFolderPermissions(request.getId());
    }

    public FilterInstanceFolderPermissionsResponse setFilterInstanceFolderPermissions(FolderPermissionSetRequest request) {
        var currentFolderRolePermission = service.getFilterInstanceFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getFilterInstanceFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(filterInstanceDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateFilterInstanceFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteFilterInstanceFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteFilterInstanceFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addFilterInstanceFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });

        return service.getFilterInstanceFolderPermissions(request.getFolderId());
    }

    public FilterTemplateFolderPermissionsResponse getFilterTemplateFolderPermissions(FolderRequest request) {

        return service.getFilterTemplateFolderPermissions(request.getId());
    }

    public FilterTemplateFolderPermissionsResponse setFilterTemplateFolderPermissions(FolderPermissionSetRequest request) {
        var currentFolderRolePermission = service.getFilterTemplateFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getFilterTemplateFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(filterTemplateDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateFilterTemplateFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteFilterTemplateFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteFilterTemplateFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addFilterTemplateFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });

        return service.getFilterTemplateFolderPermissions(request.getFolderId());
    }

    public SecurityFilterFolderPermissionsResponse getSecurityFilterFolderPermissions(FolderRequest request) {

        return service.getSecurityFilterFolderPermissions(request.getId());
    }

    public SecurityFilterFolderPermissionsResponse setSecurityFilterFolderPermissions(FolderPermissionSetRequest request) {
        var currentFolderRolePermission = service.getSecurityFilterFolderPermissions(request.getFolderId());
        var mappingFolderRolePermissions = request.getRoles().stream().collect(Collectors.toMap(RoleAddPermissionRequest::getRoleId, RoleAddPermissionRequest::getPermissions));

        Set<Long> folders = new HashSet<>();
        folders.add(request.getFolderId());

        if (request.isDownSetPermissions())
            folders.addAll(service.getSecurityFilterFolderBranch(request.getFolderId()));
        if (request.isUpSetPermissions())
            folders.addAll(securityFilterDomainService.getPathToFolder(request.getFolderId()).stream().map(FolderNodeResponse::id).toList());

        for (RolePermissionResponse r : currentFolderRolePermission.rolePermissions()) {
            if (mappingFolderRolePermissions.containsKey(r.role().getId())) {
                var newPermission = mappingFolderRolePermissions.get(r.role().getId());
                if (!newPermission.equals(r.permissions()))
                    service.updateSecurityFilterFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(r.role().getId(), newPermission));
                mappingFolderRolePermissions.remove(r.role().getId());
            } else
                service.deleteSecurityFilterFolderPermittedToRole(new ArrayList<>(folders), r.role().getId());
        }

        mappingFolderRolePermissions.forEach((key, value) -> {
            service.deleteSecurityFilterFolderPermittedToRole(new ArrayList<>(folders), key);
            service.addSecurityFilterFolderPermissions(new ArrayList<>(folders), new RoleAddPermissionRequest(key, value));
        });
        return service.getSecurityFilterFolderPermissions(request.getFolderId());
    }

    public FolderPermissionCheckResponse checkFolderPermission(PermissionCheckRequest request) {
        return service.checkFolderPermission(request);
    }
}
