package com.jhj.schedule.group.presentation;

import com.jhj.schedule.auth.security.userdetail.CustomUserDetail;
import com.jhj.schedule.group.application.GroupService;
import com.jhj.schedule.group.dto.response.GroupJobsResponseDto;
import com.jhj.schedule.job.application.JobService;
import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.job.dto.JobResponseDto;
import com.jhj.schedule.user.domain.Authority;
import com.jhj.schedule.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @InjectMocks
    private GroupController groupController;

    @Mock
    private GroupService groupService;

    @Mock
    private JobService jobService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        User user = User.builder()
                .id(1L)
                .name("테스터")
                .email("tester@test.com")
                .password("encoded")
                .authority(Authority.ROLE_USER)
                .build();

        CustomUserDetail userDetail = new CustomUserDetail(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetail, null, userDetail.getAuthorities())
        );

    }

    @Test
    @DisplayName("그룹 일정 조회 성공 - 서비스 호출 후 응답 반환")
    void getGroupJobs_success() throws Exception {
        // given
        Long groupId = 1L;
        JobRangeRequestDto jobRangeRequestDto = JobRangeRequestDto.builder()
                .year(2026)
                .month(4)
                .build();

        GroupJobsResponseDto response = GroupJobsResponseDto.builder()
                .groupId(groupId)
                .jobs(List.of(
                        JobResponseDto.builder().id(100L).title("회의").build()
                ))
                .build();

        given(jobService.findGroupJobs(any(User.class), eq(groupId), any(JobRangeRequestDto.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/groups/" + groupId + "/jobs")
                        .param("year", "2026")
                        .param("month", "4")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupId").value(groupId))
                .andExpect(jsonPath("$[0].jobs[0].id").value(100));

        verify(jobService).findGroupJobs(any(User.class), eq(groupId), any(JobRangeRequestDto.class));
    }

//    @Test
//    @DisplayName("그룹 일정 조회 성공 - 서비스 호출 후 응답 반환")
//    void groupJobs_success() throws Exception {
//        // given
//        Long groupId = 1L;
//        GroupInviteRequestDto request = new GroupInviteRequestDto(
//                List.of(new GroupMemberRequestDto())
//        );
//
//        GroupJobsResponseDto response = GroupJobsResponseDto.builder()
//                .groupId(groupId)
//                .jobs(List.of(
//                        JobResponseDto.builder().id(100L).title("회의").build()
//                ))
//                .build();
//
//        given(groupService.findGroupJobs(any(User.class), eq(groupId), any()))
//                .willReturn(List.of(response));
//
//        // when & then
//        mockMvc.perform(authenticatedPost("/api/v1/groups/" + groupId + "/jobs")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$[0].groupId").value(groupId))
//                .andExpect(jsonPath("$[0].jobs[0].id").value(100));
//
//        verify(groupService).findGroupJobs(any(User.class), eq(groupId), any());
//    }
}