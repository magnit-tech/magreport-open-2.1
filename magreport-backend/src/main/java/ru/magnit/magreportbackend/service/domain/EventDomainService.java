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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class EventDomainService {

    @Value("${magreport.event-register.event-lifetime}")
    Long lifeTimeEvents;

    @Value("${magreport.event-register.anonymous-allowed}")
    boolean anonymousUser;

    @Value("${magreport.event.enable-write-event}")
    boolean enable;

    @Value("${magreport.event.file.path}")
    String logPath;

    @Value("${magreport.event.file.name}")
    String fileName;


    private final Deque<String> stackEvents = new ArrayDeque<>();
    private final EventRepository eventRepository;

    private LocalDate currentData = LocalDate.now();
    private final AtomicInteger counterZip = new AtomicInteger(0);


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
                String.format("%s ; %s ; %s ; %s ; %s",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")),
                        userId,
                        data.getReportId(),
                        data.getJobId(),
                        data.getEvent())
        );
    }

    public void pivotEventWriter() throws IOException {

        File file = new File(logPath + fileName);
        boolean newFile = file.length() == 0;

        if (file.length() > 250000000) {
            addFileToArchive();
            Files.delete(file.toPath());
            newFile = true;
        }

        try (var output = new BufferedWriter(new FileWriter(file, true))) {

            if (newFile) {
                output.write("Data; UserId; ReportId; JobId; Event");
                output.newLine();
            }

            while (!stackEvents.isEmpty()) {
                output.write(stackEvents.pollFirst());
                output.newLine();
            }

            output.flush();
        }
    }

    @Transactional
    public void clearEventHistory() {
        if (lifeTimeEvents > 0) {
            var lastDate = LocalDateTime.now().minusHours(lifeTimeEvents);
            eventRepository.deleteAllByCreatedDateTimeBefore(lastDate);
        }
    }


    private void addFileToArchive() {

        if (!currentData.equals(LocalDate.now())) {
            currentData = LocalDate.now();
            counterZip.set(0);
        }

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream( String.format("%spivot_event.%s.%s.zip", logPath, currentData, counterZip.getAndIncrement())));
             FileInputStream fis = new FileInputStream(logPath + fileName)
        ) {
            var log = new ZipEntry(fileName);
            zout.putNextEntry(log);

            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            zout.write(buffer);
            zout.closeEntry();
        } catch (Exception ex) {
            throw new FileSystemException(ex.getMessage());
        }


    }

}
