package com.jhj.schedule.auth.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenCookieFactory {
    public static final String NAME = "refreshToken";

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;

    public ResponseCookie issue(String token, boolean secure) {
        return base(token, secure)
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .build();
    }

    public ResponseCookie expire(boolean secure) {
        return base("", secure)
                .maxAge(0)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value, boolean secure) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite("Lax"); // 또는 "Strict"
    }

}
