package com.jhj.schedule.group.dto.response;

import com.jhj.schedule.group.domain.Group;
import com.jhj.schedule.group.domain.GroupMember;
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


    public static GroupResponseDto from(Group group, List<GroupMember> members) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .members(
                        members.stream()
                                .map(GroupMemberResponseDto::from)
                                .toList()
                )
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
