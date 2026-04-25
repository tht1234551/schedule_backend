package com.jhj.schedule.group.dto;

import com.jhj.schedule.group.GroupMember;
import com.jhj.schedule.group.GroupRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupMemberResponseDto {
    private Long userId;
    private String username;
    private GroupRole role;

    public static GroupMemberResponseDto from(GroupMember groupMember) {
        return GroupMemberResponseDto.builder()
                .userId(groupMember.getUserId())
                .username(groupMember.getUserName())
                .role(groupMember.getRole())
                .build();
    }
}