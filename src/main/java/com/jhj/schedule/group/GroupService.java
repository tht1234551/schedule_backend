package com.jhj.schedule.group;

import com.jhj.schedule.group.dto.*;
import com.jhj.schedule.group.exception.MemberInvitationsException;
import com.jhj.schedule.user.User;
import com.jhj.schedule.user.UserRepository;
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
    public List<GroupSummaryResponseDto> findGroups(User user) {
        return groupRepository.findSummariesByUserId(user.getId());
    }

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

        group.setMembers(members);
        return GroupResponseDto.from(group);
    }

    /*
      1. 권한 확인: inviter가 해당 groupId의 JOINED 멤버인지. 아니면 403.
      2. 중복 필터링: 이미 그룹에 PENDING/JOINED로 존재하는 이메일은 건너뛰거나 409.
      3. 자기 자신 초대 방지: createGroup과 동일한 룰 적용.
      4. N+1 방지: userRepository.findByEmailIn(emails)로 한 번에 조회 후 Map 매칭.
      5. 부분 성공 정책: 10명 중 2명이 이미 가입돼 있으면? 실패
      6. 최종 반환값: 모두 성공인지, 일부 성공인지 전체 실패인지 반환
     */
    @Transactional
    public List<GroupMemberResponseDto> invite(User inviter, Long groupId, List<GroupMemberRequestDto> invitations) {
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);

        checkPermission(inviter, members);
        checkSelfInvite(inviter, invitations);
        checkDuplicate(invitations, members);

        groupMemberRepository.invite(inviter, groupId, invitations);
        return null;
    }

    private void checkPermission(User inviter, List<GroupMember> members) {
        GroupMember inviterMember = members.stream()
                .filter(m -> inviter.getId().equals(m.getUserId()))
                .findFirst()
                .orElseThrow(() -> new MemberInvitationsException("그룹 멤버가 아닙니다"));

        if (inviterMember.getStatus() != GroupMemberStatus.JOINED) {
            throw new MemberInvitationsException("그룹멤버가 아닌 사람은 초대 할 수 없습니다");
        }
    }

    private void checkSelfInvite(User inviter, List<GroupMemberRequestDto> invitations) {
        Optional<GroupMemberRequestDto> selfInvite = invitations.stream().filter(x -> x.getInviteEmail().equals(inviter.getEmail())).findFirst();

        if(selfInvite.isPresent()) {
            throw new MemberInvitationsException("자기 자신은 초대할 수 없습니다");
        }
    }

    private void checkDuplicate(List<GroupMemberRequestDto> invitations, List<GroupMember> members) {
        Set<String> existingEmails = members.stream()
                .map(GroupMember::getInviteEmail)
                .collect(Collectors.toSet());

        List<String> duplicates = invitations.stream()
                .map(GroupMemberRequestDto::getInviteEmail)
                .filter(existingEmails::contains)
                .toList();

        if (!duplicates.isEmpty()) {
            throw new MemberInvitationsException("이미 초대된 사람은 초대할 수 없습니다");
        }
    }
}