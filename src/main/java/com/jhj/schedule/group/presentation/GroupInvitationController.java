package com.jhj.schedule.group.presentation;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.group.application.GroupInvitationService;
import com.jhj.schedule.group.dto.request.GroupInviteRequestDto;
import com.jhj.schedule.group.dto.response.GroupMemberResponseDto;
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
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupInvitationController {

    private final GroupInvitationService groupInvitationService;

    @GetMapping("/invitations")
    public ResponseEntity<List<GroupSummaryResponseDto>> getMyInvitations(
            @CurrentUser User user
    ) {
        User user = userDetail.getUser();
        List<GroupSummaryResponseDto> invitations = groupInvitationService.findMyInvitationsGroups(user);

        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/{groupId}/invitations")
    public ResponseEntity<List<GroupMemberResponseDto>> inviteMembers(
            @CurrentUser User user,
            @PathVariable Long groupId,
            @Valid @RequestBody GroupInviteRequestDto dto
    ) {
        User inviter = userDetail.getUser();
        List<GroupMemberResponseDto> created = groupInvitationService.invite(inviter, groupId, dto.getInvitations());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{groupId}/invitations/accept")
    public ResponseEntity<GroupMemberResponseDto> acceptInvitation(
            @CurrentUser User user,
            @PathVariable Long groupId
    ) {
        User user = userDetail.getUser();
        GroupMemberResponseDto accepted = groupInvitationService.acceptInvitation(user, groupId);

        return ResponseEntity.ok(accepted);
    }

    @PostMapping("/{groupId}/invitations/decline")
    public ResponseEntity<GroupMemberResponseDto> declineInvitation(
            @CurrentUser User user,
            @PathVariable Long groupId
    ) {
        User user = userDetail.getUser();
        GroupMemberResponseDto declined = groupInvitationService.declineInvitation(user, groupId);

        return ResponseEntity.ok(declined);
    }
}