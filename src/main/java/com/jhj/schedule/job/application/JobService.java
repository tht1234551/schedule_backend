package com.jhj.schedule.job.application;


import com.jhj.schedule.group.dto.response.GroupJobsResponseDto;
import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.job.dto.JobRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.job.exception.InvalidJobPeriodException;
import com.jhj.schedule.job.exception.JobNotFoundException;
import com.jhj.schedule.job.infrastructure.JobRepository;
import com.jhj.schedule.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public List<JobResponseDto> findPersonalJobs(User user, JobRangeRequestDto request) {
        return jobRepository
                .findOverlappingJobs(user.getId(), request)
                .stream()
                .map(JobResponseDto::from)
                .toList();
    }

    @Transactional
    public JobResponseDto saveJob(Job job) {
        validatePeriod(job.getStartDate(), job.getEndDate());

        Job save = jobRepository.insert(job);
        return JobResponseDto.from(save);
    }

    @Transactional
    public JobResponseDto modifyJob(Long jobId, User user, JobRequestDto request) {
        validatePeriod(request.getStartDateTime(), request.getEndDateTime());

        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(JobNotFoundException::new);

        job.update(
                request.getTitle(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getHexColor(),
                request.getDescription(),
                request.getContentsPolicyType()
        );

        return JobResponseDto.from(jobRepository.insert(job));
    }

    @Transactional
    public void deleteJob(Long jobId, User user) {
        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(JobNotFoundException::new);
        jobRepository.delete(job.getId());
    }

    @Transactional(readOnly = true)
    public GroupJobsResponseDto findJobsByGroup(User user, Long groupId, JobRangeRequestDto range) {
        List<Job> jobs = jobRepository.findJobsByGroup(user.getId(), groupId, range);
        List<JobResponseDto> jobResponses = jobs.stream().map(JobResponseDto::from).toList();

        return GroupJobsResponseDto.builder()
                .groupId(groupId)
                .jobs(jobResponses)
                .build();
    }


    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidJobPeriodException();
        }
    }
}