package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.event.Event;
import ru.magnit.magreportbackend.domain.user.User;
import ru.magnit.magreportbackend.dto.request.event.EventPivotRegisterRequest;
import ru.magnit.magreportbackend.dto.request.event.EventRegisterRequest;
import ru.magnit.magreportbackend.exception.FileSystemException;
import ru.magnit.magreportbackend.repository.EventRepository;

import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;

@Service
@RequiredArgsConstructor
public class EventDomainService {

    @Value("${magreport.event-register.event-lifetime}")
    Long lifeTimeEvents;

    @Value("${magreport.event-register.anonymous-allowed}")
    boolean anonymousUser;

    @Value("${magreport.event.enable-write-event}")
    boolean enable;

    @Value("${magreport.event.file.name}")
    String logPath;


    private final Deque<String> stackEvents = new ArrayDeque<>();
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

    public void pivotEventRegister(EventPivotRegisterRequest data, Long userId) {
        stackEvents.addLast(
                String.format("%s | userId : %s | reportId : %s | jobId : %s | event : %s",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")),
                        userId,
                        data.getReportId(),
                        data.getJobId(),
                        data.getEvent()));
    }

    public void pivotEventWriter() {
        File file = new File(logPath);

        try (var output = new BufferedWriter(new FileWriter(file, true))) {

            while (!stackEvents.isEmpty()) {
                output.write(stackEvents.pollFirst());
                output.newLine();

            }
            output.flush();
        } catch (IOException e) {
            throw new FileSystemException(e.getMessage());
        }


    }

    @Transactional
    public void clearEventHistory() {
        if (lifeTimeEvents > 0) {
            var lastDate = LocalDateTime.now().minusHours(lifeTimeEvents);
            eventRepository.deleteAllByCreatedDateTimeBefore(lastDate);
        }
    }

}
