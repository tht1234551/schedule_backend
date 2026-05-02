package com.jhj.schedule.job.domain;

import com.jhj.schedule.job.exception.InvalidJobPeriodException;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    private Long id;
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String hexColor;
    private String description;
    private ContentsPolicyType contentsPolicyType;
    private Long userId;
    private OwnerType ownerType;
    private Long groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void validatePeriod() {
        if (startAt == null || endAt == null) {
            return;
        }

        if (startAt.isAfter(endAt)) {
            throw new InvalidJobPeriodException();
        }
    }
}