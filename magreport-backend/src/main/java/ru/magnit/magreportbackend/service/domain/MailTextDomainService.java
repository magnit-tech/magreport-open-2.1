package ru.magnit.magreportbackend.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.domain.schedule.DestinationTypeEnum;
import ru.magnit.magreportbackend.dto.request.mail.EmailSendRequest;
import ru.magnit.magreportbackend.dto.request.user.RoleRequest;
import ru.magnit.magreportbackend.dto.request.user.UserRequest;
import ru.magnit.magreportbackend.dto.response.schedule.DestinationEmailResponse;
import ru.magnit.magreportbackend.dto.response.schedule.DestinationRoleResponse;
import ru.magnit.magreportbackend.dto.response.schedule.DestinationUserResponse;
import ru.magnit.magreportbackend.dto.response.schedule.ScheduleTaskResponse;
import ru.magnit.magreportbackend.dto.response.user.UserResponse;
import ru.magnit.magreportbackend.repository.ServerMailTemplateRepository;
import ru.magnit.magreportbackend.service.EmailService;
import ru.magnit.magreportbackend.service.RoleService;
import ru.magnit.magreportbackend.service.SettingsService;
import ru.magnit.magreportbackend.service.UserService;
import ru.magnit.magreportbackend.util.Pair;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailTextDomainService {

    private final ServerMailTemplateRepository repository;
    private final EmailService emailService;
    private final UserService userService;
    private final RoleService roleService;
    private final JobTokenDomainService jobTokenDomainService;

    private final SettingsService settingsService;

    @Value("${mail.adminMailBox}")
    private String adminMailBox;

    @Value("${magreport.schedule-mail-task-changed}")
    private String scheduleMailChanged;

    @Value("${magreport.schedule-mail-task-user-error}")
    private String scheduleMailErrorUser;

    private static final String LINE_BREAK = "<br/>";


    public void sendScheduleMailExcel(String code, ScheduleTaskResponse taskResponse, List<Pair<String, File>> attachments, Pair<String, String> warning) {

        var mailText = repository.findByCode(code);
        var warningText = "";
        if (!warning.getL().isEmpty())
            warningText = updateTextForScheduleService(repository.findByCode(warning.getL()).getBody(), taskResponse, warning.getR(), "");

        emailService.sendMail(
                new EmailSendRequest(
                        updateTextForScheduleService(taskResponse.getReportTitleMail() == null ? mailText.getSubject() : taskResponse.getReportTitleMail(), taskResponse),
                        updateTextForScheduleService(taskResponse.getReportBodyMail(), taskResponse) + LINE_BREAK + warningText,
                        getEmails(DestinationTypeEnum.REPORT, taskResponse.getDestinationEmails(), taskResponse.getDestinationUsers(), taskResponse.getDestinationRoles()),
                        attachments
                ));
    }

    public void sendScheduleMailBigExcel(String code, ScheduleTaskResponse taskResponse, Long reportJobId, String linkService, Pair<String, String> warning) {

        var mailText = repository.findByCode(code);
        var warningText = !warning.getL().isEmpty() ? repository.findByCode(warning.getL()).getBody() : "";
        var emails = getEmails(DestinationTypeEnum.REPORT, taskResponse.getDestinationEmails(), taskResponse.getDestinationUsers(), taskResponse.getDestinationRoles());

        emails.forEach(email -> {
            String token = jobTokenDomainService.createJobToken(reportJobId, taskResponse.getExcelTemplate().getId(), email);
            String fileLink = linkService.replace("{reportToken}", token);


            emailService.sendMail(
                    new EmailSendRequest(
                            updateTextForScheduleService(taskResponse.getReportTitleMail() == null ? mailText.getSubject() : taskResponse.getReportTitleMail(), taskResponse),
                            updateTextForScheduleService(taskResponse.getReportBodyMail() + LINE_BREAK + mailText.getBody() + LINE_BREAK + warningText, taskResponse, warning.getR(), fileLink, reportJobId),
                            email
                    )
            );
        });
    }

    public void sendScheduleMailWeb(String code, ScheduleTaskResponse taskResponse, Long reportJobId, Pair<String, String> warning) {

        var mailText = repository.findByCode(code);

        var warningText = "";
        if (!warning.getL().isEmpty())
            warningText = updateTextForScheduleService(repository.findByCode(warning.getL()).getBody(), taskResponse, warning.getR(), "", reportJobId);

        emailService.sendMail(
                new EmailSendRequest(
                        updateTextForScheduleService(taskResponse.getReportTitleMail() == null ? mailText.getSubject() : taskResponse.getReportTitleMail(), taskResponse, warning.getR().isEmpty() ? "" : warning.getR(), "", reportJobId),
                        updateTextForScheduleService(mailText.getBody(), taskResponse, warning.getR().isEmpty() ? "" : warning.getR(), "", reportJobId) + LINE_BREAK + warningText,
                        getEmails(DestinationTypeEnum.REPORT, taskResponse.getDestinationEmails(), taskResponse.getDestinationUsers(), taskResponse.getDestinationRoles())
                )
        );
    }

    public void sendScheduleMailExpired(String code, ScheduleTaskResponse taskResponse, String link) {

        var mailText = repository.findByCode(code);

        emailService.sendMail(
                new EmailSendRequest(
                        updateTextForScheduleService(mailText.getSubject(), taskResponse),
                        updateTextForScheduleService(mailText.getBody(), taskResponse, link, ""),
                        getEmails(DestinationTypeEnum.REPORT, taskResponse.getDestinationEmails(), taskResponse.getDestinationUsers(), taskResponse.getDestinationRoles())
                )
        );

    }

    public void sendScheduleMailChanged(ScheduleTaskResponse taskResponse) {

        var mailText = repository.findByCode(scheduleMailChanged);

        emailService.sendMail(
                new EmailSendRequest(
                        updateTextForScheduleService(mailText.getSubject(), taskResponse),
                        updateTextForScheduleService(mailText.getBody(), taskResponse),
                        settingsService.getValueSetting(adminMailBox)
                )
        );

    }

    public void sendScheduleMailFailed(String code, ScheduleTaskResponse taskResponse, Long reportJobId, Pair<String, StackTraceElement[]> error) {

        var mailText = repository.findByCode(code);
        var userMailText = repository.findByCode(scheduleMailErrorUser);

        emailService.sendMail(
                new EmailSendRequest(
                        updateTextForScheduleService(mailText.getSubject(), taskResponse, reportJobId, error),
                        updateTextForScheduleService(mailText.getBody(), taskResponse, reportJobId, error),
                        settingsService.getValueSetting(adminMailBox)
                )
        );

        var emails = getEmails(DestinationTypeEnum.ERROR, taskResponse.getDestinationEmails(), taskResponse.getDestinationUsers(), taskResponse.getDestinationRoles());

        if (!emails.isEmpty()) {
            emailService.sendMail(
                    new EmailSendRequest(
                            updateTextForScheduleService(taskResponse.getErrorTitleMail() == null ? userMailText.getSubject() : taskResponse.getErrorTitleMail(), taskResponse),
                            updateTextForScheduleService(taskResponse.getErrorBodyMail() == null ? userMailText.getBody() : taskResponse.getErrorBodyMail(), taskResponse),
                            emails
                    )
            );
        }
    }

    public void sendScheduleMailDeadline(String code, ScheduleTaskResponse taskResponse, String prolongationLink) {
        var mailText = repository.findByCode(code);

        emailService.sendMail(
                new EmailSendRequest(
                        updateTextForScheduleService(mailText.getSubject(), taskResponse),
                        updateTextForScheduleService(mailText.getBody(), taskResponse, prolongationLink, ""),
                        getEmails(DestinationTypeEnum.REPORT, taskResponse.getDestinationEmails(), taskResponse.getDestinationUsers(), taskResponse.getDestinationRoles())
                )
        );
    }

    private List<String> getEmails(
            DestinationTypeEnum destinationType,
            List<DestinationEmailResponse> destinationEmails,
            List<DestinationUserResponse> destinationUsers,
            List<DestinationRoleResponse> destinationRoles) {

        var result = destinationEmails.stream()
                .filter(d -> d.getType().equals(destinationType))
                .map(DestinationEmailResponse::getValue)
                .collect(Collectors.toList());

        result.addAll(destinationRoles.stream()
                .filter(d -> d.getType().equals(destinationType))
                .map(t -> roleService.getRoleUsers(new RoleRequest().setId(t.getRoleId())))
                .flatMap(user -> user.getUsers().stream())
                .map(UserResponse::getEmail)
                .toList());

        result.addAll(destinationUsers.stream()
                .filter(d -> d.getType().equals(destinationType))
                .map(user -> userService.getUserResponse(new UserRequest(user.getUserName(), user.getDomainName())).getEmail())
                .toList());

        return result;
    }


    private String updateTextForScheduleService(String updateText, ScheduleTaskResponse taskResponse) {
        return updateTextForScheduleService(updateText, taskResponse, "", "", 0L, null);
    }

    private String updateTextForScheduleService(String updateText, ScheduleTaskResponse taskResponse, String prolongationLink, String fileLink) {
        return updateTextForScheduleService(updateText, taskResponse, prolongationLink, fileLink, 0L, null);
    }

    private String updateTextForScheduleService(String updateText, ScheduleTaskResponse taskResponse, String prolongationLink, String fileLink, Long reportJob) {
        return updateTextForScheduleService(updateText, taskResponse, prolongationLink, fileLink, reportJob, null);
    }

    private String updateTextForScheduleService(String updateText, ScheduleTaskResponse taskResponse, Long reportJob, Pair<String, StackTraceElement[]> ex) {
        return updateTextForScheduleService(updateText, taskResponse, "", "", reportJob, ex);
    }

    private String updateTextForScheduleService(String updateText, ScheduleTaskResponse taskResponse, String prolongationLink, String fileLink, Long reportJobId, Pair<String, StackTraceElement[]> ex) {

        if (updateText == null) {
            return "Ошибка! Текст не найден";
        }

        updateText = updateTextForError(updateText, ex);
        updateText = updateTextForSystemValue(updateText);

        return updateText
                .replace("{reportName}", taskResponse.getReport().getName())
                .replace("{reportId}", taskResponse.getReport().getId().toString())
                .replace("{reportJobId}", reportJobId.toString())
                .replace("{taskId}", taskResponse.getId().toString())
                .replace("{taskName}", taskResponse.getName())
                .replace("{prolongationLink}", prolongationLink)
                .replace("{taskDescription}", taskResponse.getDescription())
                .replace("{expiredDate}", taskResponse.getExpirationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .replace("{fileLink}", fileLink);
    }

    private String updateTextForError(String updateText, Pair<String, StackTraceElement[]> ex) {
        if (ex == null)
            ex = new Pair<>();

        return updateText
                .replace("{textError}", ex.getL() == null ? "-" : ex.getL())
                .replace("{stackTrace}", ex.getR() == null ? "-" : Arrays.stream(ex.getR()).map(StackTraceElement::toString).collect(Collectors.joining("</br>")));
    }

    private String updateTextForSystemValue(String updateText) {
        return updateText.replace("{currentDataTime}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
    }

}
