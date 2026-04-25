package com.jhj.schedule.auth;

import com.jhj.schedule.auth.application.AuthService;
import com.jhj.schedule.auth.dto.SignUpRequestDto;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.infrastructure.UserRepository;
import com.jhj.schedule.user.dto.UserResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        SignUpRequestDto request = SignUpRequestDto.builder()
                .name("홍길동")
                .email("test@test.com")
                .password("Test1234!")
                .build();

        // given
        String email = request.getEmail();
        String rawPassword = request.getPassword();
        String encodedPassword = "encodedPassword";

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(userRepository.insert(any(User.class))).willAnswer(i -> i.getArgument(0));

        // when
        UserResponseDto response = authService.signUp(request);

        // then
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getName()).isEqualTo(request.getName());
        verify(userRepository, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_fail_duplicateEmail() {
        // given
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("test@test.com")
                .build();

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 존재하는 이메일입니다.");

        verify(userRepository, never()).insert(any(User.class));
    }
}