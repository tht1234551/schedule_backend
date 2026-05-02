package com.jhj.schedule.job.application;

import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.JobPatch;
import com.jhj.schedule.job.dto.request.JobCreateRequestDto;
import com.jhj.schedule.job.dto.response.JobResponseDto;
import com.jhj.schedule.job.dto.request.JobUpdateRequestDto;
import com.jhj.schedule.user.domain.User;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JobMapper {

    private JobMapper() {}

    public static Job toDomain(JobCreateRequestDto request, User user) {
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


    public static JobPatch toPatch(JobUpdateRequestDto from) {
        JobPatch to = new JobPatch();

        apply(from::getTitle, to::setTitle);
        apply(from::getStartAt, to::setStartAt);
        apply(from::getEndAt, to::setEndAt);
        apply(from::getHexColor, to::setHexColor);
        apply(from::getDescription, to::setDescription);
        apply(from::getContentsPolicyType, to::setContentsPolicyType);

        return to;
    }

    public static void applyFromTo(JobPatch from, Job to) {
        applyUnwrapped(from::getTitle, to::setTitle);
        applyUnwrapped(from::getStartAt, to::setStartAt);
        applyUnwrapped(from::getEndAt, to::setEndAt);
        applyUnwrapped(from::getHexColor, to::setHexColor);
        applyUnwrapped(from::getDescription, to::setDescription);
        applyUnwrapped(from::getContentsPolicyType, to::setContentsPolicyType);
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
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private static <T> void applyUnwrapped(Supplier<JsonNullable<T>> supplier, Consumer<T> consumer) {
        JsonNullable<T> jsonNullable = supplier.get();

        if(jsonNullable.isPresent()) {
            consumer.accept(jsonNullable.get());
        }
    }

    private static <T> void apply(Supplier<JsonNullable<T>> supplier, Consumer<JsonNullable<T>> consumer) {
        JsonNullable<T> value = supplier.get();

        if (value.isPresent()) {
            consumer.accept(value);
        }
    }
}