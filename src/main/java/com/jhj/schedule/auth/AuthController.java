package com.jhj.schedule.auth;

import com.jhj.schedule.auth.dto.SignUpRequestDto;
import com.jhj.schedule.auth.exception.RefreshTokenNotFoundException;
import com.jhj.schedule.user.dto.UserResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping( "/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody SignUpRequestDto request) {
        UserResponseDto userResponseDto = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        Optional.ofNullable(refreshToken).orElseThrow(RefreshTokenNotFoundException::new);
        String newRefreshToken = authService.createRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + newRefreshToken)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        Optional.ofNullable(refreshToken)
                .ifPresent(authService::deleteToken);

        // 쿠키 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 성공");
    }
}
