package com.jhj.schedule.group.dto;

import com.jhj.schedule.group.GroupMemberEntity;
import com.jhj.schedule.group.GroupRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupMemberResponseDto {
    private Long userId;
    private String username;
    private GroupRole role;

    public static GroupMemberResponseDto from(GroupMemberEntity groupMemberEntity) {
        return GroupMemberResponseDto.builder()
                .userId(groupMemberEntity.getUser().getId())
                .username(groupMemberEntity.getUser().getName())
                .role(groupMemberEntity.getRole())
                .build();
    }
}
