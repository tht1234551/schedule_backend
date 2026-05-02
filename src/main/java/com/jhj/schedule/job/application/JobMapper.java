package com.jhj.schedule.job.application;

import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.dto.JobRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.job.dto.JobUpdateRequestDto;
import com.jhj.schedule.user.domain.User;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JobMapper {

    public static Job toDomain(JobRequestDto request, User user) {
        return Job.builder()
                .title(request.getTitle())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .hexColor(request.getHexColor())
                .description(request.getDescription())
                .contentsPolicyType(request.getContentsPolicyType())
                .userId(user.getId())
                .ownerType(request.getOwnerType())
                .groupId(request.getGroupId())
                .build();
    }

    public static void applyToDomain(JobUpdateRequestDto request, Job job) {
        apply(job::setTitle, request::getTitle);
        apply(job::setStartAt, request::getStartAt);
        apply(job::setEndAt, request::getEndAt);
        apply(job::setHexColor, request::getHexColor);
        apply(job::setDescription, request::getDescription);
        apply(job::setContentsPolicyType, request::getContentsPolicyType);
    }

    public static JobResponseDto toResponse(Job job) {
        return JobResponseDto.builder()
                .id(job.getId())
                .userId(job.getUserId())
                .title(job.getTitle())
                .startDateTime(job.getStartAt())
                .endDateTime(job.getEndAt())
                .hexColor(job.getHexColor())
                .description(job.getDescription())
                .contentsPolicyType(job.getContentsPolicyType())
                .ownerType(job.getOwnerType())
                .groupId(job.getGroupId())
                .build();
    }

    private static <T> void apply(Consumer<T> consumer, Supplier<JsonNullable<T>> supplier) {
        JsonNullable<T> value = supplier.get();

        if (value.isPresent()) {
            consumer.accept(value.orElse(null));
        }
    }
}