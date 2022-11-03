package ru.magnit.magreportbackend.service.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.magnit.magreportbackend.config.JwtPropertiesConfig;
import ru.magnit.magreportbackend.dto.request.user.LoginRequest;
import ru.magnit.magreportbackend.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtPropertiesConfig jwtPropertiesConfig;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            final var credentials = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            credentials.setUserName(credentials.getUserName().toLowerCase());

            final var authentication = new UsernamePasswordAuthenticationToken(
                    credentials.getUserName(),
                    credentials.getPassword(),
                    new ArrayList<>()
            );
            authentication.setDetails(credentials.getDomainNameUpperCase());
            return authenticationManager.authenticate(authentication);
        } catch (Exception ex) {
            log.error("Error while attempting authenticate user: " + ex.getMessage(), ex);
            throw new AuthenticationException(ex.getMessage(), ex) {
            };
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        var domainName = authResult.getDetails() == null ? null : authResult.getDetails().toString();
        var user = userService.loginUser(domainName, authResult.getName(), authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        if (user != null) {
            String token = JWT.create()
                    .withSubject(authResult.getName())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtPropertiesConfig.getValidityDuration()))
                    .withClaim("Roles", userService.getUserRoles(authResult.getName(), user.getDomain().name()))
                    .withClaim("Domain", domainName)
                    .sign(HMAC512(jwtPropertiesConfig.getSecretKey().getBytes()));

            response.addHeader(jwtPropertiesConfig.getHeaderString(), jwtPropertiesConfig.getTokenPrefix() + " " + token);
            response.addHeader("Access-Control-Expose-Headers", jwtPropertiesConfig.getHeaderString());
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        }
    }
}
