package com.springbootlearning.learningspringboot3.config;

import com.springbootlearning.learningspringboot3.dto.UserAccountDto;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app.config")
public record AppConfig(String header, String intro, List<UserAccountDto> users) {
}
