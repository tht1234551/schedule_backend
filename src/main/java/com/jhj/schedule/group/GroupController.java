package com.jhj.schedule.group;

import com.jhj.schedule.auth.userdetail.CustomUserDetail;
import com.jhj.schedule.group.dto.GroupRequestDto;
import com.jhj.schedule.group.dto.GroupResponseDto;
import com.jhj.schedule.group.dto.GroupSummaryResponseDto;
import com.jhj.schedule.user.UserEntity;
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
        UserEntity user = userDetail.getUser();
        List<GroupSummaryResponseDto> groups = groupService.findGroups(user);
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody GroupRequestDto request
    ) {
        UserEntity user = userDetail.getUser();
        GroupResponseDto groupResponseDto = groupService.createGroup(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupResponseDto);
    }


}