package com.jhj.schedule.auth;

import com.jhj.schedule.auth.dto.SignUpRequestDto;
import com.jhj.schedule.auth.jwt.JwtUtil;
import com.jhj.schedule.user.User;
import com.jhj.schedule.user.UserService;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
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

        User user = userService.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new RuntimeException("invalid refresh token"));

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