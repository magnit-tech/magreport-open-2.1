package ru.magnit.magreportbackend.dto.request.mail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.magnit.magreportbackend.util.Pair;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)

public class EmailSendRequest {

    private String subject;
    private String body;
    private List<String> to = Collections.emptyList();
    private List<String> cc = Collections.emptyList();
    private List<String> bcc = Collections.emptyList();

    private List<Pair<String, File>> attachments = Collections.emptyList();

    public boolean checkItem(){
        return !subject.isEmpty() && !body.isEmpty() && !(to.isEmpty() && cc.isEmpty() && bcc.isEmpty());
    }

    public EmailSendRequest(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public EmailSendRequest(String subject, String body, List<String> to) {
        this.subject = subject;
        this.body = body;
        this.to = to;
    }

    public EmailSendRequest(String subject, String body, String to) {
        this.subject = subject;
        this.body = body;
        this.to = Collections.singletonList(to);
    }

    public EmailSendRequest(String subject, String body, List<String> to, List<String> cc, List<String> bcc, List<Pair<String,File>> attachments) {
        this.subject = subject;
        this.body = body;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.attachments = attachments;
    }

    public EmailSendRequest(String subject, String body, List<String> to,  List<Pair<String,File>> attachments) {
        this.subject = subject;
        this.body = body;
        this.to = to;
        this.attachments = attachments;
    }

}
