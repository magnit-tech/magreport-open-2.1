package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.asm.AsmSecurityAddRequest;
import ru.magnit.magreportbackend.dto.request.asm.AsmSecurityRequest;
import ru.magnit.magreportbackend.dto.response.asm.AsmSecurityResponse;
import ru.magnit.magreportbackend.dto.response.asm.AsmSecurityShortResponse;
import ru.magnit.magreportbackend.service.domain.ExternalAuthRoleFilterRefreshService;
import ru.magnit.magreportbackend.service.domain.ExternalAuthRoleRefreshService;
import ru.magnit.magreportbackend.service.domain.ExternalAuthUserRoleRefreshService;
import ru.magnit.magreportbackend.service.domain.ExternalSecurityDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalSecurityService {

    private final ExternalSecurityDomainService domainService;
    private final ExternalAuthRoleRefreshService roleRefreshService;
    private final ExternalAuthUserRoleRefreshService userRoleRefreshService;
    private final ExternalAuthRoleFilterRefreshService roleFilterRefreshService;
    private final UserDomainService userDomainService;

    @Value("${spring.profiles.active}")
    private String profile;

    public AsmSecurityResponse addAsmSecurity(AsmSecurityAddRequest request) {

        final var currentUser = userDomainService.getCurrentUser();
        Long id = domainService.addAsmSecurity(currentUser, request);

        return domainService.getAsmSecurity(id);
    }

    public AsmSecurityResponse getAsmSecurity(AsmSecurityRequest request) {

        return domainService.getAsmSecurity(request.getId());
    }

    public List<AsmSecurityShortResponse> getAllAsmSecurity() {

        return domainService.getAllAsmSecurity();
    }

    public void deleteAsmSecurity(AsmSecurityRequest request) {
        domainService.deleteAsmSecurity(request.getId());
    }

    public void refreshAmsFilters(List<Long> idList) {

        final var filters = domainService.getAllAmsFilters(idList);

        filters.forEach( filter -> {
            try {
                roleRefreshService.refreshRoles(filter);
                userRoleRefreshService.refreshUserRoles(filter);
                roleFilterRefreshService.refreshSecurityFilters(filter);
            } catch (Exception ex){
                log.error(String.format("Failed update ASM filter (id:%s): %s", filter.getId(), ex.getMessage()));
                ex.printStackTrace();
            }
        });
    }

    public AsmSecurityResponse editAsmSecurity(AsmSecurityAddRequest request) {
        domainService.editAsmSecurity(request);

        return domainService.getAsmSecurity(request.getId());
    }

    public Boolean switchActiveAsmSecurity(AsmSecurityRequest request) {
        return domainService.switchAsmSecurity(request.getId());
    }

    @Scheduled(cron = "${magreport.asm.refresh-schedule}")
    private void scheduledRefresher() {
        log.info("ASM scheduled refresh started, profile:'" + profile + "'.");
        if (!profile.equals("prod")) return;

        final var allAsmSecurity = domainService.getAllAsmSecurity();

        final var asmIds = allAsmSecurity.stream().filter(AsmSecurityShortResponse::isActive).map(AsmSecurityShortResponse::id).toList();

        refreshAmsFilters(asmIds);
    }
}
