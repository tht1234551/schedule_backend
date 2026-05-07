package com.jhj.schedule.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jhj.schedule.auth.dto.PasswordValidation;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordUpdateRequestDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Size(
            min = PasswordValidation.MIN_LENGTH,
            max = PasswordValidation.MAX_LENGTH,
            message = PasswordValidation.LENGTH_MESSAGE
    )
    @Pattern(
            regexp = PasswordValidation.PATTERN,
            message = PasswordValidation.PATTERN_MESSAGE
    )
    private String newPassword;

    @JsonIgnore
    @AssertTrue(message = "새 비밀번호는 기존과 달라야 합니다.")
    public boolean isDifferent() {
        return currentPassword != null
                && newPassword != null
                && !currentPassword.equals(newPassword);
    }

}
