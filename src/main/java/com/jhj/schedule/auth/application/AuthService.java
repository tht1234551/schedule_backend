package com.jhj.schedule.auth.application;

import com.jhj.schedule.auth.infrastructure.RefreshTokenRepository;
import com.jhj.schedule.auth.domain.RefreshToken;
import com.jhj.schedule.auth.dto.SignUpRequestDto;
import com.jhj.schedule.auth.security.jwt.JwtUtil;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    final private UserService userService;
    final private RefreshTokenRepository refreshTokenRepository;
    final private PasswordEncoder passwordEncoder;
    final private JwtUtil jwtUtil;

    @Transactional
    public UserResponseDto signUp(SignUpRequestDto request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .avatar(request.getAvatar())
                .build();

        return UserResponseDto.from(userService.create(user));
    }

    // TODO: 정리
    @Transactional
    public String createRefreshToken(String refreshToken) {
        if (jwtUtil.invalid(refreshToken)) {
            throw new RuntimeException("invalid refresh token");
        }

        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("invalid refresh token"));

        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("refresh token expired");
        }

        User user = userService.findById(tokenEntity.getUserId());

        String email = jwtUtil.getEmail(refreshToken);
        String role = user.getAuthority().name();
        Long id = user.getId();

        return jwtUtil.createAccessToken(id, email, role);
    }

    @Transactional
    public void deleteToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}