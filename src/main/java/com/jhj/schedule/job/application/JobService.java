package com.jhj.schedule.job.application;


import com.jhj.schedule.common.exception.CustomRuntimeException;
import com.jhj.schedule.group.dto.response.GroupJobsResponseDto;
import com.jhj.schedule.group.exception.GroupAccessDeniedException;
import com.jhj.schedule.group.exception.GroupErrorCode;
import com.jhj.schedule.group.infrastructure.GroupMemberRepository;
import com.jhj.schedule.group.infrastructure.GroupRepository;
import com.jhj.schedule.job.domain.Job;
import com.jhj.schedule.job.domain.JobPatch;
import com.jhj.schedule.job.domain.OwnerType;
import com.jhj.schedule.job.dto.request.JobMonthRequestDto;
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
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public List<JobResponseDto> findPersonalJobs(User user, JobMonthRequestDto request) {
        return jobRepository
                .findOverlappingJobs(user.getId(), request)
                .stream()
                .map(JobMapper::toResponse)
                .toList();
    }

    @Transactional
    public JobResponseDto save(User user, JobCreateRequestDto requestDto) {
        verifyGroupAccessIfNeeded(user, requestDto);

        Job job = JobMapper.toDomain(requestDto, user);
        job.validatePeriod();

        Job insert = jobRepository.insert(job);
        return JobMapper.toResponse(insert);
    }

    @Transactional
    public JobResponseDto modify(Long jobId, User user, JobUpdateRequestDto request) {
        Long userId = user.getId();
        Job oldJob = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(JobNotFoundException::new);

        JobPatch patch = JobMapper.toPatch(request);
        JobMapper.applyFromTo(patch, oldJob);
        oldJob.validatePeriod();

        if (jobRepository.update(jobId, userId, patch) == 0) {
            throw new JobNotFoundException();
        }

        Job job = jobRepository.findByIdAndUserId(jobId, userId)
                .orElseThrow(JobNotFoundException::new);

        return JobMapper.toResponse(job);
    }

    @Transactional
    public void deleteJob(Long jobId, User user) {
        if (jobRepository.delete(jobId, user.getId()) == 0) {
            throw new JobNotFoundException();
        }
    }

    @Transactional(readOnly = true)
    public GroupJobsResponseDto findGroupJobs(User user, Long groupId, JobMonthRequestDto requestDto) {
        List<Job> jobs = jobRepository.findGroupJobs(user.getId(), groupId, requestDto);
        List<JobResponseDto> jobResponses = jobs.stream().map(JobMapper::toResponse).toList();

        return GroupJobsResponseDto.builder()
                .groupId(groupId)
                .jobs(jobResponses)
                .build();
    }

    private void verifyGroupAccessIfNeeded(User user, JobCreateRequestDto requestDto) {
        if (requestDto.getOwnerType() != OwnerType.GROUP) {
            return;
        }

        if (!groupMemberRepository.existsJoinedMember(requestDto.getGroupId(), user.getId())) {
            throw new GroupAccessDeniedException(GroupErrorCode.GROUP_MEMBER_NOT_FOUND);
        }
    }

}