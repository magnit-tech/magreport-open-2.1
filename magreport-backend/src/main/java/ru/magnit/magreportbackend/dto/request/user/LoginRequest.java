package ru.magnit.magreportbackend.dto.request.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    private String domainName;
    private String userName;
    private String password;

    public String getDomainNameUpperCase(){
        return domainName == null ? null : domainName.toUpperCase();
    }
}
