package com.jhj.schedule.job.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.domain.OwnerType;
import com.jhj.schedule.job.dto.JobValidation;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JobCreateRequestDto {

    @NotBlank(message = "타이틀을 입력해주세요")
    @Size(max = JobValidation.TITLE_MAX)
    private String title;

    @NotNull(message = "시작일을 입력해주세요")
    private LocalDateTime startAt;

    @NotNull(message = "종료일을 입력해주세요")
    private LocalDateTime endAt;

    @NotNull
    private OwnerType ownerType;

    private Long groupId; // ownerType==GROUP 일 때만

    @NotNull
    private ContentsPolicyType contentsPolicyType;

    @Pattern(regexp = JobValidation.HEX_COLOR_PATTERN, message = JobValidation.HEX_COLOR_MESSAGE)
    private String hexColor;

    @Size(max = JobValidation.DESCRIPTION_MAX)
    private String description;

    @JsonIgnore
    @AssertTrue(message = "GROUP 타입은 groupId가 필요하고, USER 타입은 groupId가 없어야 합니다")
    public boolean isGroupIdConsistent() {
        if (ownerType == OwnerType.GROUP) {
            return groupId != null;
        } else {
            return groupId == null; // USER일 때 groupId는 null이어야
        }
    }
}