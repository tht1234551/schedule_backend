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

        applyJsonNullable(from::getTitle, to::setTitle);
        applyJsonNullable(from::getStartAt, to::setStartAt);
        applyJsonNullable(from::getEndAt, to::setEndAt);
        applyJsonNullable(from::getHexColor, to::setHexColor);
        applyJsonNullable(from::getDescription, to::setDescription);
        applyJsonNullable(from::getContentsPolicyType, to::setContentsPolicyType);

        return to;
    }

    public static void applyFromTo(JobPatch from, Job to) {
        applyPrimitive(from::getTitle, to::setTitle);
        applyPrimitive(from::getStartAt, to::setStartAt);
        applyPrimitive(from::getEndAt, to::setEndAt);
        applyPrimitive(from::getHexColor, to::setHexColor);
        applyPrimitive(from::getDescription, to::setDescription);
        applyPrimitive(from::getContentsPolicyType, to::setContentsPolicyType);
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

    private static <T> void applyPrimitive(Supplier<JsonNullable<T>> supplier, Consumer<T> consumer) {
        JsonNullable<T> jsonNullable = supplier.get();

        if(jsonNullable.isPresent()) {
            consumer.accept(jsonNullable.get());
        }
    }

    private static <T> void applyJsonNullable(Supplier<JsonNullable<T>> supplier, Consumer<JsonNullable<T>> consumer) {
        JsonNullable<T> value = supplier.get();

        if (value.isPresent()) {
            consumer.accept(value);
        }
    }
}