package com.springbootlearning.learningspringboot3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.config.general")
public class AppConfigProperties {

    private final Security security = new Security();

    public Security getSecurity() {
        return this.security;
    }

    public static class Security {
        private String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";
        private final Authentication authentication = new Authentication();

        public Authentication getAuthentication() {
            return this.authentication;
        }

        public String getContentSecurityPolicy() {
            return this.contentSecurityPolicy;
        }

        public void setContentSecurityPolicy(String contentSecurityPolicy) {
            this.contentSecurityPolicy = contentSecurityPolicy;
        }

        public static class Authentication {
            private final Jwt jwt = new Jwt();

            public Jwt getJwt() {
                return this.jwt;
            }

            public static class Jwt {
                private String base64Secret;
                private long accessTokenValidityInSeconds = 1800L;
                private long refreshTokenValidityInSeconds = 18000L;

                public String getBase64Secret() {
                    return base64Secret;
                }

                public void setBase64Secret(String base64Secret) {
                    this.base64Secret = base64Secret;
                }

                public long getAccessTokenValidityInSeconds() {
                    return accessTokenValidityInSeconds;
                }

                public void setAccessTokenValidityInSeconds(long accessTokenValidityInSeconds) {
                    this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
                }

                public long getRefreshTokenValidityInSeconds() {
                    return refreshTokenValidityInSeconds;
                }

                public void setRefreshTokenValidityInSeconds(long refreshTokenValidityInSeconds) {
                    this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
                }
            }
        }
    }
}

