package ru.magnit.magreportbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt.properties")
public class JwtPropertiesConfig {
    private Long validityDuration;
    private String secretKey;
    private String tokenPrefix;
    private String headerString;
}
