package com.jhj.schedule.common.util;

import com.jhj.schedule.auth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    JwtUtil jwtUtil;

    @Test
    @DisplayName("토큰 생성 테스트")
    void createToken() {
        String test = jwtUtil.createRefreshToken("test@test.com");
        assertThat(test).isNotEmpty();
    }

    @Test
    @DisplayName("토큰 파싱 테스트")
    void parseToken() {
        String token = jwtUtil.createRefreshToken("test@test.com");
        String id = jwtUtil.getData(token, "id");

        assertThat(id).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("토큰 파싱 테스트2")
    void parseToken2() {
        String token = jwtUtil.createRefreshToken("test@test.com");
        Claims claims = jwtUtil.extractAllClaims(token);
        System.out.println(claims);
        String id = claims.get("id").toString();
        assertThat(id).isEqualTo("test@test.com");
    }

}