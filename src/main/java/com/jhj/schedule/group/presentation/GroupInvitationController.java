package com.jhj.schedule.group.presentation;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.group.application.GroupInvitationService;
import com.jhj.schedule.group.dto.response.GroupMemberResponseDto;
import com.jhj.schedule.group.dto.response.GroupSummaryResponseDto;
import com.jhj.schedule.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupInvitationController {

    private final GroupInvitationService groupInvitationService;

    @GetMapping("/invitations")
    public ResponseEntity<List<GroupSummaryResponseDto>> getMyInvitations(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        User user = userDetail.getUser();
        List<GroupSummaryResponseDto> invitations = groupInvitationService.findMyInvitationsGroups(user);

        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/{groupId}/invitations/accept")
    public ResponseEntity<GroupMemberResponseDto> acceptInvitation(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long groupId
    ) {
        User user = userDetail.getUser();
        GroupMemberResponseDto accepted = groupInvitationService.acceptInvitation(user, groupId);

        return ResponseEntity.ok(accepted);
    }

    @PostMapping("/{groupId}/invitations/decline")
    public ResponseEntity<GroupMemberResponseDto> declineInvitation(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long groupId
    ) {
        User user = userDetail.getUser();
        GroupMemberResponseDto declined = groupInvitationService.declineInvitation(user, groupId);

        return ResponseEntity.ok(declined);
    }
}