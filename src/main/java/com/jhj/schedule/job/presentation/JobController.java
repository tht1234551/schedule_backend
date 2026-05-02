package com.jhj.schedule.job.presentation;

import com.jhj.schedule.common.annotation.CurrentUser;
import com.jhj.schedule.job.application.JobService;
import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.job.dto.JobRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.job.dto.JobUpdateRequestDto;
import com.jhj.schedule.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    ResponseEntity<List<JobResponseDto>> getMyJobs(
            @CurrentUser User user,
            @Valid @ModelAttribute JobRangeRequestDto request) {
        List<JobResponseDto> personalJobs = jobService.findPersonalJobs(user, request);
        return ResponseEntity.ok(personalJobs);
    }

    @PostMapping
    ResponseEntity<JobResponseDto> addJob(
            @CurrentUser User user,
            @Valid @RequestBody JobRequestDto request) {
        JobResponseDto jobResponseDto = jobService.save(user, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponseDto);
    }

    @PatchMapping("/{jobId}")
    public ResponseEntity<JobResponseDto> modify(
            @CurrentUser User user,
            @PathVariable Long jobId,
            @RequestBody JobUpdateRequestDto request
    ) {
        JobResponseDto response = jobService.modify(jobId, user, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> delete(
            @CurrentUser User user,
            @PathVariable Long jobId
    ) {
        jobService.deleteJob(jobId, user);

        return ResponseEntity.noContent().build();
    }
}
