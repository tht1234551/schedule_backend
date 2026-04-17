package com.jhj.schedule.auth;

import com.jhj.schedule.auth.dto.SignUpRequestDto;
import com.jhj.schedule.auth.exception.EmailAlreadyExistsException;
import com.jhj.schedule.auth.jwt.JwtUtil;
import com.jhj.schedule.user.UserEntity;
import com.jhj.schedule.user.UserRepository;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {
    
    final private UserRepository userRepository;
    final private RefreshTokenRepository refreshTokenRepository;
    final private PasswordEncoder passwordEncoder;
    final private JwtUtil jwtUtil;

    @Transactional
    public UserResponseDto signUp(SignUpRequestDto request) {
        validateDuplicateEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity user = request.toEntity(encodedPassword);
        return UserResponseDto.from(userRepository.save(user));
    }


    @Transactional
    public void validateDuplicateEmail(String email) {
        boolean existsByEmail = userRepository.existsByEmail(email);

        if (existsByEmail) {
            throw new EmailAlreadyExistsException();
        }
    }

    @Transactional
    public String refreshToken(String refreshToken) {
        if (jwtUtil.invalid(refreshToken)) {
            throw new RuntimeException("invalid refresh token");
        }

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("invalid refresh token"));

        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("refresh token expired");
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = tokenEntity.getUser().getAuthority().name();
        Long id = tokenEntity.getUser().getId();

        return jwtUtil.createAccessToken(id, email, role);
    }

    @Transactional
    public void deleteToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
