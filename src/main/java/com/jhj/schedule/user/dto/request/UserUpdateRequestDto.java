package com.jhj.schedule.user.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jhj.schedule.user.dto.UserValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class UserUpdateRequestDto {

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<
            @NotBlank(message = UserValidation.NAME_NOT_BLANK_MESSAGE)
            @Size(max = UserValidation.NAME_MAX_LENGTH)
                    String
            > name = JsonNullable.undefined();

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private JsonNullable<String> avatar = JsonNullable.undefined();

}
