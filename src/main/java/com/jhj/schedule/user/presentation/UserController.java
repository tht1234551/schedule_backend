package com.jhj.schedule.user.presentation;

import com.jhj.schedule.common.annotation.CurrentUser;
import com.jhj.schedule.user.application.UserService;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.dto.request.UserUpdateRequestDto;
import com.jhj.schedule.user.dto.response.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@CurrentUser User user) {
        UserResponseDto result = UserResponseDto.from(user);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> modify(
            @CurrentUser User user,
            @Valid @RequestBody UserUpdateRequestDto requestDto
    ) {
        // TODO
        return null;
    }

}
