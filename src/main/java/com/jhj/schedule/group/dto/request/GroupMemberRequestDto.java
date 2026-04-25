package com.jhj.schedule.group.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GroupMemberRequestDto {
    private String inviteEmail;
}