package com.jhj.schedule.group.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    private Long id;
    private String groupName;

    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addMember(GroupMember member) {
        members.add(member);
    }

    public void removeMember(GroupMember member) {
        members.remove(member);
    }
}