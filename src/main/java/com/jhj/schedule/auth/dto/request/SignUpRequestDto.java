package com.jhj.schedule.auth.dto.request;

import com.jhj.schedule.auth.dto.PasswordValidation;
import com.jhj.schedule.user.dto.UserValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {

    @NotBlank(message = UserValidation.NAME_NOT_BLANK_MESSAGE)
    @Size(max = UserValidation.NAME_MAX_LENGTH, message = UserValidation.NAME_MAX_MESSAGE)
    private String name;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(
            min = PasswordValidation.MIN_LENGTH,
            max = PasswordValidation.MAX_LENGTH,
            message = PasswordValidation.LENGTH_MESSAGE)
    @Pattern(
            regexp = PasswordValidation.PATTERN,
            message = PasswordValidation.PATTERN_MESSAGE
    )
    private String password;

    private String avatar;
}
