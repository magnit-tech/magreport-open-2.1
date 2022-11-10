package ru.magnit.magreportbackend.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import ru.magnit.magreportbackend.dto.request.mail.EmailSendRequest;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.service.EmailService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class LogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> implements SmartLifecycle {

    @Value("${logging.magreport.destination-error}")
    String[] destinations;

    @Value("${logging.magreport.file.name}")
    private String mainLogPath;

    private final EmailService emailService;


    @Override
    protected void append(ILoggingEvent event) {

        if (event.getLevel() != Level.ERROR)
            return;

        var logs = new ArrayList<String>();

        ReversedLinesFileReader reader;
        try {
            reader = new ReversedLinesFileReader(new File(mainLogPath), null);
            for (int i = 0; i <= 150; i++) {
                var logRow = reader.readLine();
                if (logRow != null) logs.add(logRow);
            }
        } catch (IOException e) {
            throw new InvalidParametersException("Incorrect path to file logs");
        }


        Collections.reverse(logs);

        var mes = String.format(
                "<b>Дата:</b> %s<br/>" +
                        "<b>Ошибка:</b> %s <br/>" +
                        "<b>Лог:</b> %s"
                , LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), event.getMessage(), String.join("<br/>", logs));

        emailService.sendMail(new EmailSendRequest( "Магрепорт: Ошибка сервиса", mes, Arrays.stream(destinations).toList()));
    }

    @Override
    public boolean isRunning() {
        return isStarted();
    }
}
