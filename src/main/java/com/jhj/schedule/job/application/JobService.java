package com.jhj.schedule.job.application;


import com.jhj.schedule.group.dto.response.GroupJobsResponseDto;
import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.JobPatch;
import com.jhj.schedule.job.dto.request.JobRangeRequestDto;
import com.jhj.schedule.job.dto.request.JobCreateRequestDto;
import com.jhj.schedule.job.dto.response.JobResponseDto;
import com.jhj.schedule.job.dto.request.JobUpdateRequestDto;
import com.jhj.schedule.job.exception.JobNotFoundException;
import com.jhj.schedule.job.infrastructure.JobRepository;
import com.jhj.schedule.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .map(JobMapper::toResponse)
                .toList();
    }

    @Transactional
    public JobResponseDto save(User user, JobCreateRequestDto requestDto) {
        Job job = JobMapper.toDomain(requestDto, user);
        job.validatePeriod();

        Job insert = jobRepository.insert(job);
        return JobMapper.toResponse(insert);
    }

    @Transactional
    public JobResponseDto modify(Long jobId, User user, JobUpdateRequestDto request) {
        Long userId = user.getId();
        Job job = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(JobNotFoundException::new);

        JobPatch patch = JobMapper.toPatch(request);
        JobMapper.applyFromTo(patch, job);
        job.validatePeriod();

        jobRepository.update(jobId, userId, patch);
        return JobMapper.toResponse(job);
    }

    @Transactional
    public void deleteJob(Long jobId, User user) {
        Job job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(JobNotFoundException::new);
        jobRepository.delete(job.getId());
    }

    @Transactional(readOnly = true)
    public GroupJobsResponseDto findGroupJobs(User user, Long groupId, JobRangeRequestDto range) {
        List<Job> jobs = jobRepository.findGroupJobs(user.getId(), groupId, range);
        List<JobResponseDto> jobResponses = jobs.stream().map(JobMapper::toResponse).toList();

        return GroupJobsResponseDto.builder()
                .groupId(groupId)
                .jobs(jobResponses)
                .build();
    }
}