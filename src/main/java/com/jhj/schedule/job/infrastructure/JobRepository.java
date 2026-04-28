package com.jhj.schedule.job.infrastructure;

import com.jhj.schedule.group.domain.GroupMemberStatus;
import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import com.jhj.schedule.job.dto.JobRangeRequestDto;
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
                .and(JOBS.START_DATE.lt(request.getEndDate()))
                .and(JOBS.END_DATE.ge(request.getStartDate()))
                .fetch()
                .map(this::toDomain);
    }

    public List<Job> findGroupJobs(Long userId, Long groupId, JobRangeRequestDto range) {
        return dsl.select(
                JOBS.ID,
                JOBS.USER_ID,
                JOBS.START_DATE,
                JOBS.END_DATE,
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
                .and(JOBS.START_DATE.lt(range.getEndDate()))
                .and(JOBS.END_DATE.ge(range.getStartDate()))
                .fetch()
                .map(this::toDomain);
    }

    public Job insert(Job job) {
        LocalDateTime now = LocalDateTime.now();

        Long id = dsl.insertInto(JOBS)
                .set(JOBS.TITLE, job.getTitle())
                .set(JOBS.START_DATE, job.getStartDate())
                .set(JOBS.END_DATE, job.getEndDate())
                .set(JOBS.HEX_COLOR, job.getHexColor())
                .set(JOBS.DESCRIPTION, job.getDescription())
                .set(JOBS.OPEN_TYPE, job.getContentsPolicyType() != null ? job.getContentsPolicyType().name() : null)
                .set(JOBS.USER_ID, job.getUserId())
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

    public Job update(Job job) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(JOBS)
                .set(JOBS.TITLE, job.getTitle())
                .set(JOBS.START_DATE, job.getStartDate())
                .set(JOBS.END_DATE, job.getEndDate())
                .set(JOBS.HEX_COLOR, job.getHexColor())
                .set(JOBS.DESCRIPTION, job.getDescription())
                .set(JOBS.OPEN_TYPE, job.getContentsPolicyType() != null ? job.getContentsPolicyType().name() : null)
                .set(JOBS.UPDATED_AT, now)
                .where(JOBS.ID.eq(job.getId()))
                .execute();
        job.setUpdatedAt(now);

        return job;
    }

    public void delete(Long id) {
        dsl.deleteFrom(JOBS).where(JOBS.ID.eq(id)).execute();
    }

    private Job toDomain(Record r) {
        return Job.builder()
                .id(r.get(JOBS.ID))
                .title(r.get(JOBS.TITLE))
                .startDate(r.get(JOBS.START_DATE))
                .endDate(r.get(JOBS.END_DATE))
                .hexColor(r.get(JOBS.HEX_COLOR))
                .description(r.get(JOBS.DESCRIPTION))
                .contentsPolicyType(r.get(JOBS.OPEN_TYPE) != null ? ContentsPolicyType.valueOf(r.get(JOBS.OPEN_TYPE)) : null)
                .userId(r.get(JOBS.USER_ID))
                .createdAt(r.get(JOBS.CREATED_AT))
                .updatedAt(r.get(JOBS.UPDATED_AT))
                .build();
    }
}