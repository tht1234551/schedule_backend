package com.jhj.schedule.group.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GroupSummaryResponseDto {
    private Long id;
    private String groupName;
}