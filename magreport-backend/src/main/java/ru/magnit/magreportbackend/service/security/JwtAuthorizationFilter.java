package ru.magnit.magreportbackend.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.magnit.magreportbackend.config.AuthConfig;
import ru.magnit.magreportbackend.config.JwtPropertiesConfig;
import ru.magnit.magreportbackend.domain.user.UserStatusEnum;
import ru.magnit.magreportbackend.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static ru.magnit.magreportbackend.domain.user.UserStatusEnum.LOGGED_OFF;


@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserService userService;
    private final AuthConfig authConfig;
    private final JwtPropertiesConfig jwtPropertiesConfig;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserService userService, JwtPropertiesConfig jwtPropertiesConfig, AuthConfig authConfig) {
        super(authenticationManager);
        this.userService = userService;
        this.jwtPropertiesConfig = jwtPropertiesConfig;
        this.authConfig = authConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(jwtPropertiesConfig.getHeaderString());

        if (token == null || !token.startsWith(jwtPropertiesConfig.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(token);

        if (authenticationToken == null) {
            form401Message(response);
            return;
        }

        if (isLoggedOff(response, authenticationToken)) return;


        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

    private boolean isLoggedOff(HttpServletResponse response, UsernamePasswordAuthenticationToken authenticationToken) {
        if (authenticationToken == null) return false;

        String userName = authenticationToken.getName();
        UserStatusEnum userStatus = userService.getUserStatus(userName, authenticationToken.getDetails().toString());

        if (userStatus == null || userStatus == LOGGED_OFF) {
            form401Message(response);
            return true;
        }

        return false;
    }

    private void form401Message(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            response.getOutputStream().println("{\"success\": false,\"message\": \"Authorization required\",\"data\":{\"status\":401}}");
        } catch (IOException ex) {
            log.error("Error while trying to write response body:\n" + ex.getMessage(), ex);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        DecodedJWT decodedJWT = null;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(jwtPropertiesConfig.getSecretKey()))
                    .build()
                    .verify(token.replace(jwtPropertiesConfig.getTokenPrefix(), "").trim());
        } catch (Exception ex){
            log.warn(ex.getMessage());
        }

        if (decodedJWT == null) return null;

        String userName = decodedJWT.getSubject();
        String domainName = decodedJWT.getClaim("Domain").asString();
        domainName = domainName == null ? authConfig.getDefaultDomain() : domainName;
        List<GrantedAuthority> roles = userService.getUserAuthorities(userName, domainName);

        var result = new UsernamePasswordAuthenticationToken(userName, null, roles);
        result.setDetails(domainName);

        return result;
    }
}
