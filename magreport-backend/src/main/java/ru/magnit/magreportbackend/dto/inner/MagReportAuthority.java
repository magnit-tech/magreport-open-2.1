package ru.magnit.magreportbackend.dto.inner;

import org.springframework.security.core.GrantedAuthority;


public record MagReportAuthority(
    String authority
) implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return authority;
    }
}
