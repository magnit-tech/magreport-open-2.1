package ru.magnit.magreportadmin;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class SslConfig {

    @Bean
    public ClientHttpConnector customHttpClient() throws SSLException {
        final var sslContext = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        final var httpClient = HttpClient.create().secure(
            ssl -> ssl.sslContext(sslContext)
        );
        return new ReactorClientHttpConnector(httpClient);    }
}
