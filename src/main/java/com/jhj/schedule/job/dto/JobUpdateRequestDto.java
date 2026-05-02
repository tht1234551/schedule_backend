package com.jhj.schedule.job.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.domain.OwnerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class JobUpdateRequestDto {

    @Schema(nullable = true)
    private JsonNullable<String> title = JsonNullable.undefined();

    @Schema(nullable = true)
    private JsonNullable<LocalDateTime> startAt = JsonNullable.undefined();

    @Schema(nullable = true)
    private JsonNullable<LocalDateTime> endAt  = JsonNullable.undefined();

    @Schema(nullable = true)
    private JsonNullable<String> hexColor = JsonNullable.undefined();

    @Schema(nullable = true)
    private JsonNullable<String> description = JsonNullable.undefined();

    @Schema(nullable = true)
    private JsonNullable<ContentsPolicyType> contentsPolicyType = JsonNullable.undefined();

}
