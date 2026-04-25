package com.jhj.schedule.group.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequestDto {
    private String groupName;

    @Builder.Default
    private List<GroupMemberRequestDto> members = new ArrayList<>();
}
