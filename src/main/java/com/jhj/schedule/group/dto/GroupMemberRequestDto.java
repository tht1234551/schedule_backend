package com.jhj.schedule.group.dto;

import com.jhj.schedule.group.GroupRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupMemberRequestDto {
    private Long userId;
    private GroupRole role;
}