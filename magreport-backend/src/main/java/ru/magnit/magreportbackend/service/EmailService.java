package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.MailSenderFactory;
import ru.magnit.magreportbackend.dto.request.mail.EmailSendRequest;
import ru.magnit.magreportbackend.dto.request.mail.ListEmailsCheckRequest;
import ru.magnit.magreportbackend.dto.response.mail.ListEmailsCheckResponse;
import ru.magnit.magreportbackend.exception.InvalidApplicationSettings;
import ru.magnit.magreportbackend.exception.InvalidParametersException;
import ru.magnit.magreportbackend.util.Pair;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailSenderFactory mailSenderFactory;
    private final SettingsService settingsService;

    @Value("${mailAddressFrom}")
    String fromCode;

    @Value("${magreport.mail.permitted-domains}")
    String permittedDomainsCode;

    @Value("${magreport.mail.send-emails}")
    boolean sendEmails;

    @Value("${magreport.mail.tag-subject}")
    String tag;

    @Value("${magreport.mail.party-size-users}")
    Long partySize;

    @Value("${magreport.mail.pause-between-send-party}")
    Long pauseBetweenSendParty;


    private static final String ERROR_MESSAGE = "Error to send email";

    public void sendMail(EmailSendRequest request) {

        if (!sendEmails) {
            log.info("The ability to send messages is disabled");
            return;
        }

        if (request.checkItem()) {
            var mailSender = getMailSender();

            var from = settingsService.getValueSetting(fromCode);

            var to = new ArrayList<String>();
            var cc = new ArrayList<String>();
            var bcc = new ArrayList<String>();

            var destinations = checkAndFilterEmails(request.getTo()).stream().map(s -> new Pair<>(s, "TO")).collect(Collectors.toList());
            destinations.addAll(checkAndFilterEmails(request.getCc()).stream().map(s -> new Pair<>(s, "CC")).toList());
            destinations.addAll(checkAndFilterEmails(request.getBcc()).stream().map(s -> new Pair<>(s, "BCC")).toList());

            destinations.forEach(d -> {

                if (to.size() + cc.size() + bcc.size() == partySize) {
                    send(request, mailSender, mailSender.createMimeMessage(), from, to, cc, bcc);
                    to.clear();
                    cc.clear();
                    bcc.clear();


                    try {
                        Thread.sleep(pauseBetweenSendParty * 1000);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                        Thread.currentThread().interrupt();
                    }

                }

                switch (d.getR()) {
                    case "TO" -> to.add(d.getL());
                    case "CC" -> cc.add(d.getL());
                    case "BCC" -> bcc.add(d.getL());
                    default -> throw new IllegalStateException("Unexpected type destinations: " + d.getR());
                }
            });

            if (!to.isEmpty() || !cc.isEmpty() || !bcc.isEmpty())
                send(request, mailSender, mailSender.createMimeMessage(), from, to, cc, bcc);

        } else {
            throw new InvalidParametersException("Request not contains destinations");
        }
    }


    private void send(EmailSendRequest request, JavaMailSender mailSender, MimeMessage msg, String from, ArrayList<String> to, ArrayList<String> cc, ArrayList<String> bcc) {
        try {
            var mimeMessage = getMimeMessageHelper(request, msg, from);
            mimeMessage.setTo(to.toArray(new String[0]));
            mimeMessage.setCc(cc.toArray(new String[0]));
            mimeMessage.setBcc(bcc.toArray(new String[0]));

            mailSender.send(msg);


        } catch (MessagingException e) {
            log.error(ERROR_MESSAGE, e);
            log.error("List emails TO: " + String.join(", ", to));
            log.error("List emails CC: " + String.join(", ", cc));
            log.error("List emails BCC: " + String.join(", ", bcc));
        }
    }

    public ListEmailsCheckResponse checkEmails(ListEmailsCheckRequest request) {
        var emails = new ArrayList<>(request.getEmails().stream().map(String::toLowerCase).toList());
        var goodEmails = checkAndFilterEmails(request.getEmails());
        emails.removeAll(goodEmails);
        return new ListEmailsCheckResponse().setEmails(emails);

    }

    private JavaMailSender getMailSender() {
        try {
            return mailSenderFactory.getJavaMailSender();
        } catch (Exception ex) {
            throw new InvalidApplicationSettings("Error create mail sender", ex);
        }
    }

    private List<String> checkAndFilterEmails(List<String> emails) {
        var badEmail = emails
                .stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                .distinct()
                .toList();

        if (!badEmail.isEmpty()) {
            log.warn("These email addresses are invalid: " + String.join(", ", badEmail));
            emails.removeAll(badEmail);
        }

        var permittedDomains = Arrays.stream(
                        settingsService
                                .getValueSetting(permittedDomainsCode)
                                .replace(" ", "")
                                .toLowerCase()
                                .split(",")
                )
                .toList();

        if (!permittedDomains.get(0).isBlank())
            emails = emails
                    .stream()
                    .map(String::toLowerCase)
                    .filter(email -> permittedDomains.stream().anyMatch(email::contains))
                    .toList();


        return emails.stream().filter(Objects::nonNull).distinct().toList();
    }

    private MimeMessageHelper getMimeMessageHelper(EmailSendRequest request, MimeMessage msg, String from) throws MessagingException {
        var mimeMessage = new MimeMessageHelper(msg, true, "UTF-8");
        mimeMessage.setFrom(from);
        mimeMessage.setSubject(String.format("%s %s", tag, request.getSubject()));
        mimeMessage.setText(request.getBody(), true);

        request.getAttachments().forEach(a -> {
            try {
                mimeMessage.addAttachment(a.getL(), a.getR());
            } catch (MessagingException e) {
                log.error(ERROR_MESSAGE, e);
            }
        });

        return mimeMessage;
    }
}
