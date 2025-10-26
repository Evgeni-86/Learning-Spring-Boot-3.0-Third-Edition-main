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
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long jwtValidityInMilliseconds;

    public JwtTokenProvider(JwtEncoder jwtEncoder,
                            JwtDecoder jwtDecoder,
                            AppConfigProperties appConfigProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtValidityInMilliseconds = appConfigProperties.getSecurity()
                .getAuthentication().getJwt().getTokenValidityInSeconds() * 1000;
    }

    public String createToken(String username, List<String> roles) {
        Instant now = Instant.now();
        Instant validity = now.plusMillis(jwtValidityInMilliseconds);

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username)
                .claim("roles", roles)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(SecurityJwtConfiguration.JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
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
