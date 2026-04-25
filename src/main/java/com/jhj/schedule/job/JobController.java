package com.jhj.schedule.job;

import com.jhj.schedule.auth.userdetail.CustomUserDetail;
import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.job.dto.JobRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/personal")
    ResponseEntity<List<JobResponseDto>> personal(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @ModelAttribute JobRangeRequestDto request) {
        List<JobResponseDto> personalJobs = jobService.findPersonalJobs(userDetail.getUser(), request);
        return ResponseEntity.ok(personalJobs);
    }

    @PostMapping
    ResponseEntity<JobResponseDto> addJob(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody JobRequestDto request) {
        User user = userDetail.getUser();
        JobResponseDto jobResponseDto = jobService.saveJob(request.toEntity(user));

        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponseDto);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponseDto> modify(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long jobId,
            @Valid @RequestBody JobRequestDto request
    ) {
        User user = userDetail.getUser();
        JobResponseDto response = jobService.modifyJob(jobId, user, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long jobId
    ) {
        User user = userDetail.getUser();
        jobService.deleteJob(jobId, user);

        return ResponseEntity.noContent().build();
    }
}
