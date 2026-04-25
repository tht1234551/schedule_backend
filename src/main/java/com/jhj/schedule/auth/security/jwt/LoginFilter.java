package com.jhj.schedule.auth.security.jwt;

import com.jhj.schedule.auth.domain.RefreshToken;
import com.jhj.schedule.auth.infrastructure.RefreshTokenRepository;
import com.jhj.schedule.auth.dto.LoginRequestDto;
import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    final private AuthenticationManager authenticationManager;
    final private RefreshTokenRepository refreshTokenRepository;
    final private JwtUtil jwtUtil;

    /**
     * 로그인 요청 시 사용자 인증 처리
     */
    @Override
    @NonNull
    public Authentication attemptAuthentication(HttpServletRequest req, @NonNull HttpServletResponse res) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequestDto dto = null;

        try {
            dto = objectMapper.readValue(req.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login request", e);
        }

        String email = dto.getEmail();
        String password = dto.getPassword();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

        // AuthenticationManager를 통해 인증 수행
        return authenticationManager.authenticate(authRequest);
    }

    /**
     * 로그인 성공 시 JWT 토큰 발급
     */
    @Override

    protected void successfulAuthentication(@NonNull HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
        CustomUserDetail customUserDetail = (CustomUserDetail) auth.getPrincipal();
        User user = customUserDetail.getUser();

        String role = customUserDetail.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        String email = user.getEmail();
        Long id = user.getId();

        String refreshToken = jwtUtil.createRefreshToken(email);
        String accessToken = jwtUtil.createAccessToken(id, email, role);

        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .expiryDate(expiryDate)
                .build();

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.insert(refreshTokenEntity);

        res.addHeader("Authorization", "Bearer " + accessToken); // JWT를 Authorization 헤더에 추가
        res.setStatus(HttpServletResponse.SC_OK);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        cookie.setSecure(req.isSecure());

        res.addCookie(cookie);
    }

    /**
     * 로그인 실패 시 401 응답 반환
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) {
        res.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 Unauthorized
    }
}