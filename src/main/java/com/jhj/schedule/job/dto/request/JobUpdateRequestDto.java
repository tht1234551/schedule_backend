package com.jhj.schedule.job.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.dto.JobValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<
            @NotBlank @Size(max = JobValidation.TITLE_MAX) String
            > title = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<LocalDateTime> startAt = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<LocalDateTime> endAt = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<
            @Pattern(
                    regexp = JobValidation.HEX_COLOR_PATTERN,
                    message = JobValidation.HEX_COLOR_MESSAGE) String
            > hexColor = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<
            @Size(max = JobValidation.DESCRIPTION_MAX) String
            > description = JsonNullable.undefined();

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<
            @NotNull(message = "공개 정책 필드는 지울 수 없습니다.") ContentsPolicyType
            > contentsPolicyType = JsonNullable.undefined();

    @JsonIgnore
    @AssertFalse(message = "수정할 데이터가 없습니다.")
    public boolean isEmpty() {
        return !title.isPresent()
                && !startAt.isPresent()
                && !endAt.isPresent()
                && !hexColor.isPresent()
                && !description.isPresent()
                && !contentsPolicyType.isPresent();
    }

}
