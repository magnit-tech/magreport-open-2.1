package ru.magnit.magreportbackend.dto.inner.olap;

import lombok.Getter;
import lombok.Setter;
import ru.magnit.magreportbackend.dto.response.user.UserNameResponse;

import java.time.LocalDateTime;

@Getter
@Setter
public class OlapUserRequestLog {

    private LocalDateTime dateTime = LocalDateTime.now();
    private String url;

    private String domain;
    private String user;
    private Object request;


    public OlapUserRequestLog(String url, Object request, UserNameResponse username) {
        this.url = url;
        this.request = request;
        this.user = username.name();
        this.domain = username.domain();
    }
}
