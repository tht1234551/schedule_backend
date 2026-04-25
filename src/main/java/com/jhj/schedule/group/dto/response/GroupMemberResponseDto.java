package com.jhj.schedule.group.dto.response;

import com.jhj.schedule.group.domain.GroupMember;
import com.jhj.schedule.group.domain.GroupRole;
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