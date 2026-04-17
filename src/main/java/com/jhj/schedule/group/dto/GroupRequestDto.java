package com.jhj.schedule.group.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GroupRequestDto {
    private String groupName;
    private List<GroupMemberRequestDto> members;
}
