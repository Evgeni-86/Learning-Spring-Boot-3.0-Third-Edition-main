package com.springbootlearning.learningspringboot3.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;


    public JwtTokenProvider(JwtEncoder jwtEncoder,
                            JwtDecoder jwtDecoder,
                            AppConfigProperties appConfigProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.accessTokenValidityInMilliseconds = appConfigProperties.getSecurity()
                .getAuthentication().getJwt().getAccessTokenValidityInSeconds() * 1000;
        this.refreshTokenValidityInMilliseconds = appConfigProperties.getSecurity()
                .getAuthentication().getJwt().getRefreshTokenValidityInSeconds() * 1000;
    }

    // Access Token (короткоживущий)
    public String createAccessToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant validity = now.plusMillis(accessTokenValidityInMilliseconds);

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username)
                .claim("roles", roles)
                .claim("type", "access")
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SecurityJwtConfiguration.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    // Refresh Token (долгоживущий)
    public String createRefreshToken(String username) {
        Instant now = Instant.now();
        Instant validity = now.plusMillis(refreshTokenValidityInMilliseconds);

        // Генерируем уникальный ID для Refresh Token
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username)
                .claim("type", "refresh") // Тип - refresh
                .claim("jti", tokenId) // JWT ID для уникальности
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SecurityJwtConfiguration.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    // Проверяем, является ли токен Refresh Token'ом
    public boolean isRefreshToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String tokenType = jwt.getClaim("type");
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    // Получаем username из Refresh Token (без ролей)
    public String getUsernameFromRefreshToken(String refreshToken) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);

            // Проверяем что это действительно refresh token
            String tokenType = jwt.getClaim("type");
            if (!"refresh".equals(tokenType)) {
                throw new BadCredentialsException("Not a refresh token");
            }

            return jwt.getSubject();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token", e);
        }
    }

    // Получаем JTI (JWT ID) из токена
    public String getTokenId(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaim("jti");
        } catch (Exception e) {
            return null;
        }
    }

    public Authentication getAuthentication(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = jwt.getSubject();

        @SuppressWarnings("unchecked")
        List<String> roles = jwt.getClaim("roles");

        List<GrantedAuthority> authorities = roles != null ?
                roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            return jwtDecoder.decode(token).getSubject();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT token", e);
        }
    }
}
