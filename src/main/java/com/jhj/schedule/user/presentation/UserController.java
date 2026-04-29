package com.jhj.schedule.user.presentation;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.common.annotation.CurrentUser;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@CurrentUser User user) {
        UserResponseDto result = UserResponseDto.from(user);

        return ResponseEntity.ok(result);
    }

}
