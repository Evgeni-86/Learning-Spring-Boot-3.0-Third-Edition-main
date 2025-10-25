package com.springbootlearning.learningspringboot3.config;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import com.springbootlearning.learningspringboot3.repository.UserManagementRepository;
import com.springbootlearning.learningspringboot3.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Bean
    CommandLineRunner initUsers(UserManagementRepository repository, AppConfig appConfig) {
        return args -> {
            repository.saveAll(appConfig.users());
        };
    }

    @Bean
    UserDetailsService userService(UserRepository repo) {
        return username -> repo.findByUsername(username).asUser();
    }

    @Bean
    SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/login").permitAll()
                                .requestMatchers("/", "/search").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/new-video", "/delete/**", "/api/**").authenticated()
                                .anyRequest().denyAll()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

//    @Bean
//    @ConfigurationPropertiesBinding
//    Converter<String, GrantedAuthority> converter() {
//        return new Converter<String, GrantedAuthority>() {
//            @Override
//            public GrantedAuthority convert(String source) {
//                return new SimpleGrantedAuthority(source);
//            }
//        };
//    }

    interface GrantedAuthorityCnv extends Converter<String, GrantedAuthority> {
    }

    @Bean
    @ConfigurationPropertiesBinding
    GrantedAuthorityCnv converter() {
        return SimpleGrantedAuthority::new;
    }
}
