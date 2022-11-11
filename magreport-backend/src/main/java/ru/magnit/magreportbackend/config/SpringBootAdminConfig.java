package ru.magnit.magreportbackend.config;

import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class SpringBootAdminConfig {

    private final RestTemplate restTemplate;

    @Bean
    public BlockingRegistrationClient registrationClient() {
        return new BlockingRegistrationClient(restTemplate);
    }
}
