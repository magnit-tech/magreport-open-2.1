package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.event.Event;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.request.event.EventRegisterRequest;
import ru.magnit.magreportbackend.repository.EventRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventDomainService {

    @Value("${magreport.event-register.event-lifetime}")
    Long lifeTimeEvents;

    @Value("${magreport.event-register.anonymous-allowed}")
    boolean anonymousUser;


    private final EventRepository eventRepository;

    @Transactional
    public void eventRegister(EventRegisterRequest data, String ipAddress, Long userId) {

        if (userId == null && !anonymousUser) return;

        var event = new Event()
                .setEventType(data.getTypeEvent())
                .setUser(userId == null ? null : new User().setId(userId))
                .setAdditionally(data.getAdditionally())
                .setIpAddress(ipAddress);

        eventRepository.save(event);
    }

    @Transactional
    public void clearEventHistory() {
        if(lifeTimeEvents > 0) {
            var lastDate = LocalDateTime.now().minusHours(lifeTimeEvents);
            eventRepository.deleteAllByCreatedDateTimeBefore(lastDate);
        }
    }

}
