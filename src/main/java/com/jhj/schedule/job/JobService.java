package com.jhj.schedule.job;


import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.job.dto.JobRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.job.exception.InvalidJobPeriodException;
import com.jhj.schedule.job.exception.JobNotFoundException;
import com.jhj.schedule.user.UserEntity;
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
    public List<JobResponseDto> findPersonalJobs(UserEntity userEntity, JobRangeRequestDto request) {
        return jobRepository
                .findOverlappingJobs(userEntity, request.getStartDate(), request.getEndDate())
                .stream()
                .map(JobResponseDto::from)
                .toList();
    }

    @Transactional
    public JobResponseDto saveJob(JobEntity job) {
        validatePeriod(job.getStartDate(), job.getEndDate());

        JobEntity save = jobRepository.save(job);
        return JobResponseDto.from(save);
    }

    @Transactional
    public JobResponseDto modifyJob(Long jobId, UserEntity user, JobRequestDto request) {
        validatePeriod(request.getStartDateTime(), request.getEndDateTime());

        JobEntity job = jobRepository.findByIdAndUser(jobId, user)
                .orElseThrow(JobNotFoundException::new);

        job.update(
                request.getTitle(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getHexColor(),
                request.getDescription(),
                request.getOpenType()
        );

        return JobResponseDto.from(job);
    }

    @Transactional
    public void deleteJob(Long jobId, UserEntity user) {
        JobEntity job = jobRepository.findByIdAndUser(jobId, user)
                .orElseThrow(JobNotFoundException::new);
        jobRepository.delete(job);
    }



    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidJobPeriodException();
        }
    }
}