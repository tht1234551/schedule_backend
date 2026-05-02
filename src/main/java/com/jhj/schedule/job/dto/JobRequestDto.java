package com.jhj.schedule.job.dto;

import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.domain.OwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JobRequestDto {

    @NotBlank(message = "타이틀을 입력해주세요")
    private String title;

    @NotNull(message = "시작일을 입력해주세요")
    private LocalDateTime startAt;

    @NotNull(message = "종료일을 입력해주세요")
    private LocalDateTime endAt;

    @NotNull private OwnerType ownerType;

    private Long groupId; // ownerType==GROUP 일 때만

    private String hexColor;
    private String description;
    private ContentsPolicyType contentsPolicyType;
}