package com.jhj.schedule.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {
    private Long id;
    private Long groupId;
    private Long userId;
    private String userName;
    private GroupRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String inviteEmail;
    private GroupMemberStatus status;
}