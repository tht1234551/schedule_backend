package com.jhj.schedule.job;


import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.job.dto.JobRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.job.exception.InvalidJobPeriodException;
import com.jhj.schedule.job.exception.JobNotFoundException;
import com.jhj.schedule.user.User;
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
                .findOverlappingJobs(user.getId(), request.getStartDate(), request.getEndDate())
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
    public JobResponseDto modifyJob(Long jobId, User user, JobRequestDto request) {
        validatePeriod(request.getStartDateTime(), request.getEndDateTime());

        JobEntity job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(JobNotFoundException::new);

        job.update(
                request.getTitle(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getHexColor(),
                request.getDescription(),
                request.getOpenType()
        );

        return JobResponseDto.from(jobRepository.save(job));
    }

    @Transactional
    public void deleteJob(Long jobId, User user) {
        JobEntity job = jobRepository.findByIdAndUserId(jobId, user.getId())
                .orElseThrow(JobNotFoundException::new);
        jobRepository.delete(job.getId());
    }



    private void validatePeriod(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new InvalidJobPeriodException();
        }
    }
}