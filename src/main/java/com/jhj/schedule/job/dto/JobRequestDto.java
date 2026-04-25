package com.jhj.schedule.job.dto;

import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.OpenType;
import com.jhj.schedule.user.domain.User;
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
    private LocalDateTime startDateTime;

    @NotNull(message = "종료일을 입력해주세요")
    private LocalDateTime endDateTime;

    private String hexColor;
    private String description;
    private OpenType openType;

    public Job toEntity(User user) {
        return Job.builder()
                .title(title)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .hexColor(hexColor)
                .description(description)
                .openType(openType)
                .userId(user.getId())
                .build();
    }
}
