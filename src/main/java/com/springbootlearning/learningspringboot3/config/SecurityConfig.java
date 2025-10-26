package com.springbootlearning.learningspringboot3.config;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import com.springbootlearning.learningspringboot3.repository.UserManagementRepository;
import com.springbootlearning.learningspringboot3.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.List;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final AppConfigProperties appConfigProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityMetersService metersService;

    public SecurityConfig(AppConfigProperties appConfigProperties,
                          JwtTokenProvider jwtTokenProvider,
                          SecurityMetersService metersService) {
        this.appConfigProperties = appConfigProperties;
        this.jwtTokenProvider = jwtTokenProvider;
        this.metersService = metersService;
    }

    @Bean
    CommandLineRunner initUsers(UserManagementRepository repository, AppConfig appConfig, PasswordEncoder passwordEncoder) {
        return args -> {
            List<UserAccount> userAccounts = appConfig.users().stream()
                    .map(u -> u.toEntity(passwordEncoder))
                    .toList();
            repository.saveAll(userAccounts);
        };
    }

    @Bean
    UserDetailsService userService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))
                .asUser();
    }

    @Bean
    SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider, metersService), UsernamePasswordAuthenticationFilter.class)
                .headers(headers ->
                        headers
                                .contentSecurityPolicy(csp -> csp.policyDirectives(appConfigProperties.getSecurity().getContentSecurityPolicy()))
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                .permissionsPolicyHeader(permissions ->
                                        permissions.policy(
                                                "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                                        )
                                )
                )
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/login").permitAll()
                                .requestMatchers("/refresh").permitAll()
                                .requestMatchers("/logout").permitAll()
                                .requestMatchers("/me").permitAll()
                                .requestMatchers("/status").permitAll()
                                .requestMatchers("/", "/search").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/new-video", "/delete/**", "/api/**").authenticated()
                                .anyRequest().denyAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
