package com.jhj.schedule.job.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobRangeRequestDto {

    @Min(1970)
    @Max(2100)
    private int year;

    @Min(1)
    @Max(12)
    private int month;

    public LocalDateTime getStartDate() {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.of(year, month, 1, 0, 0)
                .plusMonths(1);
    }
}
