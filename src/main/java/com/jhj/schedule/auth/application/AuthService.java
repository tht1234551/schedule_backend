package com.jhj.schedule.auth.application;

import com.jhj.schedule.auth.domain.RefreshToken;
import com.jhj.schedule.auth.dto.IssuedTokens;
import com.jhj.schedule.auth.dto.SignUpRequestDto;
import com.jhj.schedule.auth.exception.RefreshTokenNotFoundException;
import com.jhj.schedule.auth.infrastructure.RefreshTokenRepository;
import com.jhj.schedule.auth.security.jwt.JwtUtil;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
    public void deleteToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Transactional
    public IssuedTokens issueTokens(Long userId) {
        return rotateTokens(userId);
    }

    @Transactional
    public IssuedTokens reissueTokens(String oldRefreshToken) {
        Long userId = jwtUtil.getUserId(oldRefreshToken);
        refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);

        return rotateTokens(userId);
    }

    private IssuedTokens rotateTokens(Long userId) {
        String refreshToken = jwtUtil.createRefreshToken(userId);
        String accessToken = jwtUtil.createAccessToken(userId);

        RefreshToken refreshTokenDomain = RefreshToken.builder()
                .userId(userId)
                .token(refreshToken)
                .expiryDate(jwtUtil.getRefreshTokenExpiry())
                .build();

        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.insert(refreshTokenDomain);

        return new IssuedTokens(accessToken, refreshToken);
    }
}