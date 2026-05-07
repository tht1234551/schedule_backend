package com.jhj.schedule.auth.presentation;

import com.jhj.schedule.auth.application.AuthService;
import com.jhj.schedule.auth.dto.IssuedTokens;
import com.jhj.schedule.auth.dto.request.SignUpRequestDto;
import com.jhj.schedule.auth.security.jwt.RefreshTokenCookieFactory;
import com.jhj.schedule.common.annotation.CurrentUser;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.auth.dto.request.PasswordUpdateRequestDto;
import com.jhj.schedule.user.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody SignUpRequestDto request) {
        UserResponseDto userResponseDto = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = RefreshTokenCookieFactory.NAME) String refreshToken,
            HttpServletRequest request
    ) {
        IssuedTokens issuedTokens = authService.reissueTokens(refreshToken);
        ResponseCookie refreshTokenCookie = refreshTokenCookieFactory.issue(issuedTokens.getRefreshToken(), request.isSecure());

        return ResponseEntity.ok()
                .headers((httpHeaders) -> httpHeaders.setBearerAuth(issuedTokens.getAccessToken()))
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = RefreshTokenCookieFactory.NAME, required = false) String refreshToken,
            HttpServletRequest request
    ) {
        Optional.ofNullable(refreshToken)
                .ifPresent(authService::deleteToken);
        ResponseCookie expire = refreshTokenCookieFactory.expire(request.isSecure());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, expire.toString())
                .build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @CurrentUser User user,
            @Valid @RequestBody PasswordUpdateRequestDto requestDto
    ) {
        authService.changePassword(user, requestDto);

        return ResponseEntity.noContent().build();
    }
}
