package com.jhj.schedule.job.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
    private JsonNullable<String> title = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<LocalDateTime> startAt = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<LocalDateTime> endAt  = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<String> hexColor = JsonNullable.undefined();

    @Schema(nullable = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<String> description = JsonNullable.undefined();

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<ContentsPolicyType> contentsPolicyType = JsonNullable.undefined();

    @JsonIgnore
    @AssertTrue(message = "타이틀은 지울 수 없습니다")
    public boolean isTitleNotNull() {
        return checkNotNull(title);
    }

    @JsonIgnore
    @AssertTrue(message = "공개 정책 필드는 지울 수 없습니다.")
    public boolean isContentsPolicyTypeNotNull() {
        return checkNotNull(contentsPolicyType);
    }

    private <T> boolean checkNotNull(JsonNullable<T> jsonNullable) {
        return !jsonNullable.isPresent() || jsonNullable.get() != null;
    }
}
