package com.jhj.schedule.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;

    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24;

    // JWT Token 발급
    public String createToken(Claims claims, long expireTimeMs) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String createAccessToken(Long id, String email, String role) {
        Claims claims = Jwts.claims()
                .add("id", id)
                .add("email", email)
                .add("role", role)
                .build();

        return createToken(claims, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(String email) {
        Claims claims = Jwts.claims()
                .add("email", email)
                .build();

        return createToken(claims, REFRESH_TOKEN_EXPIRATION);
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean invalid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public Long getId(String token) {
        return Long.parseLong(getData(token, "id"));
    }

    public String getEmail(String token) {
        return getData(token, "email");
    }

    public String getData(String token, String field) {
        return extractAllClaims(token).get(field).toString();
    }

    // 밝급된 Token이 만료 시간이 지났는지 체크
    public boolean isExpired(String token) {
        Date expiredDate = extractAllClaims(token).getExpiration();
        // Token의 만료 날짜가 지금보다 이전인지 check
        return expiredDate.before(new Date());
    }
}