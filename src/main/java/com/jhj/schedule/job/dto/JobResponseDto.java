package com.jhj.schedule.job.dto;

import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.domain.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class JobResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ContentsPolicyType contentsPolicyType;
    private String hexColor;
    private String description;
    private OwnerType ownerType;
    private Long groupId;
}