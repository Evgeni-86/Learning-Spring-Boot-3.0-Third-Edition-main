package com.springbootlearning.learningspringboot3.web;

import com.springbootlearning.learningspringboot3.config.JwtTokenProvider;
import com.springbootlearning.learningspringboot3.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Аутентифицируем пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Получаем роли пользователя
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Создаем ОБА токена
            String accessToken = jwtTokenProvider.createAccessToken(loginRequest.getUsername(), roles);
            String refreshToken = jwtTokenProvider.createRefreshToken(loginRequest.getUsername());

            // Возвращаем оба токена
            return ResponseEntity.ok(new AuthResponse(
                    accessToken,
                    refreshToken,
                    loginRequest.getUsername(),
                    roles
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Invalid username or password"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Authentication failed"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        try {
            String refreshToken = refreshRequest.getRefreshToken();

            // Проверяем что токен валиден
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: Invalid refresh token"));
            }

            // Проверяем что это действительно refresh token
            if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: Not a refresh token"));
            }

            // Получаем username из refresh token
            String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);

            // Здесь нужно загрузить пользователя и его роли из БД
            // Для простоты будем использовать текущую аутентификацию
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            List<String> roles = currentAuth != null ?
                    currentAuth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()) :
                    Arrays.asList("ROLE_USER"); // fallback

            // Создаем новую пару токенов
            String newAccessToken = jwtTokenProvider.createAccessToken(username, roles);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

            // Возвращаем новые токены
            return ResponseEntity.ok(new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    username,
                    roles
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Error: Invalid refresh token"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error: Token refresh failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        // В stateless подходе просто сообщаем об успешном logout
        // Клиент должен удалить токены со своей стороны
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }

    // Существующие методы остаются
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                Authentication auth = jwtTokenProvider.getAuthentication(token);

                List<String> roles = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new AuthResponse(token, null, username, roles));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Error: Invalid token"));
    }

    @GetMapping("/status")
    public ResponseEntity<MessageResponse> getAuthStatus() {
        return ResponseEntity.ok(new MessageResponse("Authentication service is running"));
    }
}
