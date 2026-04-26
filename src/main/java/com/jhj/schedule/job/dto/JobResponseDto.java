package com.jhj.schedule.job.dto;

import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class JobResponseDto {
    private Long id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ContentsPolicyType contentsPolicyType;
    private String hexColor;
    private String description;

    public static JobResponseDto from(Job job) {
        return JobResponseDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .startDateTime(job.getStartDate())
                .endDateTime(job.getEndDate())
                .hexColor(job.getHexColor())
                .description(job.getDescription())
                .contentsPolicyType(job.getContentsPolicyType())
                .build();
    }
}
