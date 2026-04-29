package com.jhj.schedule.group.application;

import com.jhj.schedule.group.domain.Group;
import com.jhj.schedule.group.domain.GroupMember;
import com.jhj.schedule.group.domain.GroupMemberStatus;
import com.jhj.schedule.group.domain.GroupRole;
import com.jhj.schedule.group.dto.request.GroupMemberRequestDto;
import com.jhj.schedule.group.dto.request.GroupRequestDto;
import com.jhj.schedule.group.dto.response.GroupJobsResponseDto;
import com.jhj.schedule.group.dto.response.GroupMemberResponseDto;
import com.jhj.schedule.group.dto.response.GroupResponseDto;
import com.jhj.schedule.group.dto.response.GroupSummaryResponseDto;
import com.jhj.schedule.group.exception.GroupErrorCode;
import com.jhj.schedule.group.exception.MemberInvitationsException;
import com.jhj.schedule.group.infrastructure.GroupMemberRepository;
import com.jhj.schedule.group.infrastructure.GroupRepository;
import com.jhj.schedule.job.dto.JobRangeRequestDto;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<GroupSummaryResponseDto> findMyGroups(User user) {
        return groupRepository.findSummariesByUserId(user.getId());
    }


    // TODO: 초대처럼 중복처리같은 유효성 체크
    @Transactional
    public GroupResponseDto createGroup(User creator, GroupRequestDto dto) {
        Group group = groupRepository.insert(
                Group.builder()
                        .groupName(dto.getGroupName())
                        .build()
        );

        Map<String, GroupMember> membersMap = new HashMap<>();

        GroupMember ownerMember = GroupMember.builder()
                .groupId(group.getId())
                .userId(creator.getId())
                .userName(creator.getName())
                .role(GroupRole.ADMIN)
                .status(GroupMemberStatus.JOINED)
                .build();

        membersMap.put(creator.getEmail(), ownerMember);

        dto.getMembers()
                .stream()
                .filter(x -> !x.getInviteEmail().equals(creator.getEmail()))
                .forEach(memberDto -> {
                    Optional<User> optionalUser = userRepository.findByEmail(memberDto.getInviteEmail());

                    GroupMember member = GroupMember.builder()
                            .groupId(group.getId())
                            .userId(optionalUser.map(User::getId).orElse(null))
                            .userName(optionalUser.map(User::getName).orElse(null))
                            .inviteEmail(memberDto.getInviteEmail())
                            .role(GroupRole.MEMBER)
                            .status(GroupMemberStatus.PENDING)
                            .build();

                    membersMap.put(memberDto.getInviteEmail(), member);
                });

        List<GroupMember> members = new ArrayList<>(membersMap.values());

        groupMemberRepository.insertAll(members);

        return GroupResponseDto.from(group, members);
    }


}