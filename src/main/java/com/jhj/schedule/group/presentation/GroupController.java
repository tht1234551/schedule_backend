package com.jhj.schedule.group.presentation;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.group.application.GroupService;
import com.jhj.schedule.group.dto.request.GroupInviteRequestDto;
import com.jhj.schedule.group.dto.request.GroupRequestDto;
import com.jhj.schedule.group.dto.response.GroupMemberResponseDto;
import com.jhj.schedule.group.dto.response.GroupResponseDto;
import com.jhj.schedule.group.dto.response.GroupSummaryResponseDto;
import com.jhj.schedule.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    ResponseEntity<List<GroupSummaryResponseDto>> getGroup(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        User user = userDetail.getUser();
        List<GroupSummaryResponseDto> groups = groupService.findGroups(user);
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody GroupRequestDto dto
    ) {
        User user = userDetail.getUser();
        GroupResponseDto groupResponseDto = groupService.createGroup(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupResponseDto);
    }

    @PostMapping("/{groupId}/invitations")
    public ResponseEntity<List<GroupMemberResponseDto>> invite(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long groupId,
            @Valid @RequestBody GroupInviteRequestDto dto
    ) {
        User inviter = userDetail.getUser();
        List<GroupMemberResponseDto> created = groupService.invite(inviter, groupId, dto.getInvitations());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}