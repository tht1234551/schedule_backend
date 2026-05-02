package com.jhj.schedule.group.dto.response;

import com.jhj.schedule.job.dto.response.JobResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupJobsResponseDto {
    private Long groupId;
    private List<JobResponseDto> jobs;
}

