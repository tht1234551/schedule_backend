package com.jhj.schedule.group.dto;

import com.jhj.schedule.group.GroupEntity;
import com.jhj.schedule.group.GroupMemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class GroupResponseDto {
    private Long id;
    private String groupName;
    private List<GroupMemberResponseDto> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static GroupResponseDto from(GroupEntity groupEntity) {
        return GroupResponseDto.builder()
                .id(groupEntity.getId())
                .groupName(groupEntity.getGroupName())
                .members(
                        groupEntity.getMembers().stream()
                        .map(GroupMemberResponseDto::from)
                        .toList()
                )
                .createdAt(groupEntity.getCreatedAt())
                .updatedAt(groupEntity.getUpdatedAt())
                .build();
    }
}
