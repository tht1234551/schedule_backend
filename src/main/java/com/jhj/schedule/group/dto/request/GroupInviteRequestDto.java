package com.jhj.schedule.group.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupInviteRequestDto {
    @NotEmpty
    @Valid
    private List<GroupMemberRequestDto> invitations;
}
