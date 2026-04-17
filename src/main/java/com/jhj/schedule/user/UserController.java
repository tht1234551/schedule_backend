package com.jhj.schedule.user;

import com.jhj.schedule.auth.userdetail.CustomUserDetail;
import com.jhj.schedule.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping( "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        UserEntity jwtUser = userDetail.getUser();
        UserEntity userEntity = userRepository.findByEmail(jwtUser.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원"));
        return ResponseEntity.ok(UserResponseDto.from(userEntity));
    }

}
