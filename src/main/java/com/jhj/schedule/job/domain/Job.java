package com.jhj.schedule.job.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String hexColor;
    private String description;
    private ContentsPolicyType contentsPolicyType;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String title,
                       LocalDateTime startDate,
                       LocalDateTime endDate,
                       String hexColor,
                       String description,
                       ContentsPolicyType contentsPolicyType) {

        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hexColor = hexColor;
        this.description = description;
        this.contentsPolicyType = contentsPolicyType;
    }
}