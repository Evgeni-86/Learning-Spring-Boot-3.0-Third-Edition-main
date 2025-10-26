package com.springbootlearning.learningspringboot3.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityMetersService metersService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, SecurityMetersService metersService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.metersService = metersService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                handleJwtException(e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtException(Exception e) {
        if (e.getMessage().contains("Invalid signature")) {
            metersService.trackTokenInvalidSignature();
        } else if (e.getMessage().contains("Jwt expired at")) {
            metersService.trackTokenExpired();
        } else if (e.getMessage().contains("Malformed token")) {
            metersService.trackTokenMalformed();
        }
        System.out.println("JWT authentication error: " + e.getMessage());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
