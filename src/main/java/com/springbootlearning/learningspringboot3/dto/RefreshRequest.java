package com.springbootlearning.learningspringboot3.dto;

import jakarta.validation.constraints.NotBlank;

// Для refresh запроса
public class RefreshRequest {

    @NotBlank
    private String refreshToken;

    // Конструкторы
    public RefreshRequest() {
    }

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Геттер и сеттер
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

