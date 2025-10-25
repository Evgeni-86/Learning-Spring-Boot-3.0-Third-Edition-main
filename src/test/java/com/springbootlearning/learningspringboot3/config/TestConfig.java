package com.springbootlearning.learningspringboot3.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@TestConfiguration
public class TestConfig {
    interface GrantedAuthorityCnvTest extends Converter<String, GrantedAuthority> {
    }

    @Bean
    @ConfigurationPropertiesBinding
    GrantedAuthorityCnvTest converter() {
        return SimpleGrantedAuthority::new;
    }
}
