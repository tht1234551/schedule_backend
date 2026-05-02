package com.jhj.schedule.job.domain;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobPatch {
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<LocalDateTime> startAt = JsonNullable.undefined();
    private JsonNullable<LocalDateTime> endAt = JsonNullable.undefined();
    private JsonNullable<String> hexColor = JsonNullable.undefined();
    private JsonNullable<String> description = JsonNullable.undefined();
    private JsonNullable<ContentsPolicyType> contentsPolicyType = JsonNullable.undefined();
}
