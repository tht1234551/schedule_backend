package com.jhj.schedule.job.dto;

import com.jhj.schedule.job.JobEntity;
import com.jhj.schedule.job.OpenType;
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
    private OpenType openType;
    private String hexColor;
    private String description;

    public static JobResponseDto from(JobEntity jobEntity) {
        return JobResponseDto.builder()
                .id(jobEntity.getId())
                .title(jobEntity.getTitle())
                .startDateTime(jobEntity.getStartDate())
                .endDateTime(jobEntity.getEndDate())
                .hexColor(jobEntity.getHexColor())
                .description(jobEntity.getDescription())
                .openType(jobEntity.getOpenType())
                .build();
    }
}
