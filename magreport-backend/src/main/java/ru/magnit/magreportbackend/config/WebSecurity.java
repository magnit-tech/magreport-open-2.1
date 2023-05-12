package ru.magnit.magreportbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.magnit.magreportbackend.service.UserService;
import ru.magnit.magreportbackend.service.security.JwtAuthenticationFilter;
import ru.magnit.magreportbackend.service.security.JwtAuthorizationFilter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.magnit.magreportbackend.controller.AdminController.DATA_LINEAGE_PATH;
import static ru.magnit.magreportbackend.controller.DerivedFieldController.DERIVED_FIELD_GET_ALL_BY_REPORT;
import static ru.magnit.magreportbackend.controller.EventController.EVENT_REGISTER;
import static ru.magnit.magreportbackend.controller.FilterInstanceController.FILTER_INSTANCE_GET_CHILD_NODES;
import static ru.magnit.magreportbackend.controller.FilterInstanceController.FILTER_INSTANCE_GET_VALUES;
import static ru.magnit.magreportbackend.controller.FilterReportController.REPORT_FILTER_CHECK_VALUES;
import static ru.magnit.magreportbackend.controller.FilterReportController.REPORT_FILTER_GET_CHILD_NODES;
import static ru.magnit.magreportbackend.controller.FolderController.FOLDER_GET;
import static ru.magnit.magreportbackend.controller.FolderController.FOLDER_SEARCH;
import static ru.magnit.magreportbackend.controller.FolderPermissionsController.FOLDER_PERMISSION_CHECK;
import static ru.magnit.magreportbackend.controller.OlapController.OLAP_GET_EXTERNAL_SERVICES;
import static ru.magnit.magreportbackend.controller.OlapController.OLAP_GET_PIVOT_TABLE_EXCEL_GET;
import static ru.magnit.magreportbackend.controller.OlapController.OLAP_REGISTER_EXTERNAL_SERVICE;
import static ru.magnit.magreportbackend.controller.ReportController.REPORT_ADD_FAVORITES;
import static ru.magnit.magreportbackend.controller.ReportController.REPORT_DELETE_FAVORITES;
import static ru.magnit.magreportbackend.controller.ReportController.REPORT_ENCRYPT;
import static ru.magnit.magreportbackend.controller.ReportController.REPORT_GET;
import static ru.magnit.magreportbackend.controller.ReportController.REPORT_GET_FAVORITES;
import static ru.magnit.magreportbackend.controller.ReportJobController.*;
import static ru.magnit.magreportbackend.controller.ScheduleController.SCHEDULE_TASK_GET_EXCEL_REPORT;
import static ru.magnit.magreportbackend.controller.ScheduleController.SCHEDULE_TASK_MANUAL_START;
import static ru.magnit.magreportbackend.controller.ScheduleController.SCHEDULE_TASK_PROLONGATION;
import static ru.magnit.magreportbackend.controller.UserController.USERS_GET;
import static ru.magnit.magreportbackend.controller.UserController.USERS_GET_ONE;
import static ru.magnit.magreportbackend.controller.UserController.USERS_WHO_AM_I;
import static ru.magnit.magreportbackend.controller.UserServicesController.DOMAIN_LIST_SERVICE;
import static ru.magnit.magreportbackend.domain.user.SystemRoles.ADMIN;
import static ru.magnit.magreportbackend.domain.user.SystemRoles.DEVELOPER;
import static ru.magnit.magreportbackend.domain.user.SystemRoles.USER;

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtPropertiesConfig jwtPropertiesConfig;
    private final AuthConfig authConfig;
    @Value("${magreport.h2.console.whitelist}")
    private String[] adminIpArray;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final var jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, userService, jwtPropertiesConfig);
        final var jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, userService, jwtPropertiesConfig, authConfig);

        http
                .headers().frameOptions().disable()
                .and()
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/api/v1/users/authenticate").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/h2-console/**").access(getIpAccessString(adminIpArray))
                .antMatchers(REPORT_JOB_REPORT_STREAM).permitAll()
                .antMatchers(REPORT_JOB_GET_EXCEL_REPORT_GET).permitAll()
                .antMatchers(OLAP_GET_PIVOT_TABLE_EXCEL_GET).permitAll()
                .antMatchers(OLAP_REGISTER_EXTERNAL_SERVICE).permitAll()
                .antMatchers(OLAP_GET_EXTERNAL_SERVICES).hasAnyAuthority(ADMIN.name())
                .antMatchers(REPORT_ENCRYPT).hasAnyAuthority(ADMIN.name())
                .antMatchers(DOMAIN_LIST_SERVICE).permitAll()
                .antMatchers(EVENT_REGISTER).permitAll()
                .antMatchers(SCHEDULE_TASK_PROLONGATION, SCHEDULE_TASK_GET_EXCEL_REPORT, SCHEDULE_TASK_MANUAL_START).permitAll()
                .antMatchers(USERS_GET).authenticated()
                .antMatchers(REPORT_JOB_GET_ALL_JOBS).hasAnyAuthority(ADMIN.name())
                .antMatchers("/api/v1/report-job/**").hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers("/api/v1/olap/**").hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(FOLDER_GET).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(FOLDER_SEARCH).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers("/api/v1/folder/**").hasAnyAuthority(ADMIN.name(), DEVELOPER.name())
                .antMatchers(REPORT_ADD_FAVORITES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(REPORT_DELETE_FAVORITES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(REPORT_GET_FAVORITES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(REPORT_GET).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(USERS_WHO_AM_I).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(USERS_GET_ONE).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers("/api/v1/users/**").hasAnyAuthority(ADMIN.name())
                .antMatchers(REPORT_FILTER_CHECK_VALUES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(REPORT_FILTER_GET_CHILD_NODES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(FILTER_INSTANCE_GET_VALUES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(FILTER_INSTANCE_GET_CHILD_NODES).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(DATA_LINEAGE_PATH).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(DERIVED_FIELD_GET_ALL_BY_REPORT).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers(FOLDER_PERMISSION_CHECK).hasAnyAuthority(ADMIN.name(), DEVELOPER.name(), USER.name())
                .antMatchers("/api/v1/**").hasAnyAuthority(ADMIN.name(), DEVELOPER.name())
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationManager(authenticationManager)
                .addFilter(jwtAuthenticationFilter)
                .addFilter(jwtAuthorizationFilter)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.applyPermitDefaultValues();
        configuration.addExposedHeader(HttpHeaders.AUTHORIZATION);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private static String getIpAccessString(String[] ipAddressArray) {
        String result = Stream
                .of(ipAddressArray)
                .map(ip -> "hasIpAddress('" + ip + "')")
                .collect(Collectors.joining(" or "));

        result = "isAnonymous() and (" + result + ")";

        log.debug("IP filter for h2-console: " + result);

        return result;
    }
}
