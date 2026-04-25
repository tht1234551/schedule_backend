package com.jhj.schedule.user.presentation;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        User jwtUser = userDetail.getUser();
        User user = userService.findByEmail(jwtUser.getEmail());
        UserResponseDto result = UserResponseDto.from(user);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/invitations")
    public ResponseEntity<UserResponseDto> invitations(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        User jwtUser = userDetail.getUser();

        return ResponseEntity.ok(null);
    }

    @PostMapping("/me/invitations/{invitationId}/accept")
    public ResponseEntity<UserResponseDto> invitationsAccept(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long invitationId
    ) {
        User jwtUser = userDetail.getUser();

        return ResponseEntity.ok(null);
    }

    @PostMapping("/me/invitations/{invitationId}/decline")
    public ResponseEntity<UserResponseDto> invitationsDecline(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long invitationId
    ) {
        User jwtUser = userDetail.getUser();

        return ResponseEntity.ok(null);
    }

}
