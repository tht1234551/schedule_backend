package com.jhj.schedule.job.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JobMonthRequestDto {

    @Min(1970)
    @Max(2100)
    private int year;

    @Min(1)
    @Max(12)
    private int month;

    @JsonIgnore
    public LocalDateTime getStartOfMonth() {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }

    @JsonIgnore
    public LocalDateTime getStartOfNextMonth() {
        return LocalDateTime.of(year, month, 1, 0, 0)
                .plusMonths(1);
    }
}
