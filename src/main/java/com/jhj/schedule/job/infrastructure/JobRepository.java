package com.jhj.schedule.job.infrastructure;

import com.jhj.schedule.group.domain.GroupMemberStatus;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.JobPatch;
import com.jhj.schedule.job.dto.request.JobRangeRequestDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jhj.schedule.jooq.Tables.GROUP_MEMBERS;
import static com.jhj.schedule.jooq.Tables.JOBS;

@Repository
@RequiredArgsConstructor
public class JobRepository {

    private final DSLContext dsl;

    public Optional<Job> findByIdAndUserId(Long id, Long userId) {
        return dsl.selectFrom(JOBS)
                .where(JOBS.ID.eq(id))
                .and(JOBS.USER_ID.eq(userId))
                .fetchOptional()
                .map(this::toDomain);
    }

    public List<Job> findOverlappingJobs(Long userId, JobRangeRequestDto request) {
        return dsl.selectFrom(JOBS)
                .where(JOBS.USER_ID.eq(userId))
                .and(JOBS.START_AT.lt(request.getEndDate()))
                .and(JOBS.END_AT.ge(request.getStartDate()))
                .fetch()
                .map(this::toDomain);
    }

    public List<Job> findGroupJobs(Long userId, Long groupId, JobRangeRequestDto range) {
        return dsl.select(
                        JOBS.ID,
                        JOBS.USER_ID,
                        JOBS.START_AT,
                        JOBS.END_AT,
                        JOBS.HEX_COLOR,
                        JOBS.OPEN_TYPE,
                        JOBS.CREATED_AT,
                        JOBS.UPDATED_AT,
                        DSL
                                .when(JOBS.USER_ID.eq(userId), JOBS.TITLE)
                                .when(JOBS.OPEN_TYPE.eq(ContentsPolicyType.PUBLIC.name()), JOBS.TITLE)
                                .when(JOBS.OPEN_TYPE.eq(ContentsPolicyType.MASKED.name()),
                                        DSL.val(ContentsPolicyType.MASKED.name()))
                                .as(JOBS.TITLE),
                        DSL
                                .when(JOBS.USER_ID.eq(userId), JOBS.DESCRIPTION)
                                .when(JOBS.OPEN_TYPE.eq(ContentsPolicyType.PUBLIC.name()), JOBS.DESCRIPTION)
                                .when(JOBS.OPEN_TYPE.eq(ContentsPolicyType.MASKED.name()), DSL.val(""))
                                .as(JOBS.DESCRIPTION)
                )
                .from(JOBS)
                .join(GROUP_MEMBERS).on(GROUP_MEMBERS.USER_ID.eq(JOBS.USER_ID))
                .where(GROUP_MEMBERS.GROUP_ID.eq(groupId))
                .and(GROUP_MEMBERS.STATUS.eq(GroupMemberStatus.JOINED.name()))
                .and(JOBS.USER_ID.eq(userId).or(JOBS.OPEN_TYPE.ne(ContentsPolicyType.PRIVATE.name())))
                .and(JOBS.START_AT.lt(range.getEndDate()))
                .and(JOBS.END_AT.ge(range.getStartDate()))
                .fetch()
                .map(this::toDomain);
    }

    public Job insert(Job job) {
        LocalDateTime now = LocalDateTime.now();

        Long id = dsl.insertInto(JOBS)
                .set(JOBS.TITLE, job.getTitle())
                .set(JOBS.START_AT, job.getStartAt())
                .set(JOBS.END_AT, job.getEndAt())
                .set(JOBS.HEX_COLOR, job.getHexColor())
                .set(JOBS.DESCRIPTION, job.getDescription())
                .set(JOBS.OPEN_TYPE, job.getContentsPolicyType() != null ? job.getContentsPolicyType().name() : null)
                .set(JOBS.USER_ID, job.getUserId())
                .set(JOBS.OWNER_TYPE, job.getOwnerType().name())
                .set(JOBS.GROUP_ID, job.getGroupId())
                .set(JOBS.CREATED_AT, now)
                .set(JOBS.UPDATED_AT, now)
                .returningResult(JOBS.ID)
                .fetchOne()
                .value1();

        job.setId(id);
        job.setCreatedAt(now);
        job.setUpdatedAt(now);

        return job;
    }

    public void update(Long jobId, Long userId, JobPatch patch) {
        LocalDateTime now = LocalDateTime.now();

        var step = dsl.update(JOBS).set(JOBS.UPDATED_AT, now);

        if (patch.getTitle().isPresent()) {
            step = step.set(JOBS.TITLE, patch.getTitle().get());
        }

        if (patch.getStartAt().isPresent()) {
            step = step.set(JOBS.START_AT, patch.getStartAt().get());
        }

        if (patch.getEndAt().isPresent()) {
            step = step.set(JOBS.END_AT, patch.getEndAt().get());
        }

        if (patch.getHexColor().isPresent()) {
            step = step.set(JOBS.HEX_COLOR, patch.getHexColor().get());
        }

        if (patch.getDescription().isPresent()) {
            step = step.set(JOBS.DESCRIPTION, patch.getDescription().get());
        }

        if (patch.getContentsPolicyType().isPresent()) {
            step = step.set(JOBS.OPEN_TYPE, patch.getContentsPolicyType().get().name());
        }

        step
                .where(JOBS.ID.eq(jobId))
                .and(JOBS.USER_ID.eq(userId))
                .execute();
    }

    public void delete(Long id) {
        dsl.deleteFrom(JOBS).where(JOBS.ID.eq(id)).execute();
    }

    private Job toDomain(Record r) {
        return Job.builder()
                .id(r.get(JOBS.ID))
                .title(r.get(JOBS.TITLE))
                .startAt(r.get(JOBS.START_AT))
                .endAt(r.get(JOBS.END_AT))
                .hexColor(r.get(JOBS.HEX_COLOR))
                .description(r.get(JOBS.DESCRIPTION))
                .contentsPolicyType(r.get(JOBS.OPEN_TYPE) != null ? ContentsPolicyType.valueOf(r.get(JOBS.OPEN_TYPE)) : null)
                .userId(r.get(JOBS.USER_ID))
                .createdAt(r.get(JOBS.CREATED_AT))
                .updatedAt(r.get(JOBS.UPDATED_AT))
                .build();
    }
}