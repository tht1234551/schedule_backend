package com.jhj.schedule.job.infrastructure;

import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.ContentsPolicyType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                .map(this::toEntity);
    }

    public List<Job> findOverlappingJobs(Long userId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        return dsl.selectFrom(JOBS)
                .where(JOBS.USER_ID.eq(userId))
                .and(JOBS.START_DATE.lt(monthEnd))
                .and(JOBS.END_DATE.ge(monthStart))
                .fetch()
                .map(this::toEntity);
    }

    public Job save(Job job) {
        LocalDateTime now = LocalDateTime.now();
        if (job.getId() == null) {
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
        } else {
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
        }
        return job;
    }

    public void delete(Long id) {
        dsl.deleteFrom(JOBS).where(JOBS.ID.eq(id)).execute();
    }

    private Job toEntity(Record r) {
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