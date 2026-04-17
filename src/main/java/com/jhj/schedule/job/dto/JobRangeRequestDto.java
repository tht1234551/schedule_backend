package com.jhj.schedule.job.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobRangeRequestDto {

    @Min(1970)
    @Max(2100)
    private int year = LocalDateTime.now().getYear();

    @Min(1)
    @Max(12)
    private int month = LocalDateTime.now().getMonthValue();

    public LocalDateTime getStartDate() {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.of(year, month, 1, 0, 0)
                .plusMonths(1);
    }
}
