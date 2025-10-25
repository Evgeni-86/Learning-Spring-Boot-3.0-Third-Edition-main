package com.springbootlearning.learningspringboot3.config;

import com.springbootlearning.learningspringboot3.domain.UserAccount;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app.config")
public record AppConfig(String header, String intro, List<UserAccount> users) {
}
