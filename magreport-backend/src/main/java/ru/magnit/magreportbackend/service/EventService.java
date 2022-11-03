package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.dto.request.event.EventRegisterRequest;
import ru.magnit.magreportbackend.service.domain.EventDomainService;
import ru.magnit.magreportbackend.service.domain.UserDomainService;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserDomainService userService;
    private final EventDomainService eventDomainService;

    public void eventRegister(EventRegisterRequest request, String ipAddress) {
        var currentUser = userService.getCurrentUser();
        eventDomainService.eventRegister(request, ipAddress, currentUser == null ? null : currentUser.getId());
    }

    @Scheduled(cron = "${magreport.jobengine.history-clear-schedule}")
    private void clearEventHistory() {
        eventDomainService.clearEventHistory();
    }
}
