package com.jhj.schedule.group.application;

import com.jhj.schedule.group.domain.GroupMember;
import com.jhj.schedule.group.domain.GroupMemberStatus;
import com.jhj.schedule.group.dto.request.GroupMemberRequestDto;
import com.jhj.schedule.group.dto.response.GroupMemberResponseDto;
import com.jhj.schedule.group.dto.response.GroupSummaryResponseDto;
import com.jhj.schedule.group.exception.GroupErrorCode;
import com.jhj.schedule.group.exception.MemberInvitationsException;
import com.jhj.schedule.group.infrastructure.GroupMemberRepository;
import com.jhj.schedule.group.infrastructure.GroupRepository;
import com.jhj.schedule.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupInvitationService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

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

    @Transactional(readOnly = true)
    public List<GroupSummaryResponseDto> findMyInvitationsGroups(User user) {
        return groupRepository.findMyInvitationsGroups(user.getId());
    }

    @Transactional
    public GroupMemberResponseDto acceptInvitation(User user, Long groupId) {
        return groupMemberRepository.acceptInvitation(user.getId(), groupId)
                .map(GroupMemberResponseDto::from)
                .orElseThrow(() -> new MemberInvitationsException(GroupErrorCode.INVITATION_NOT_FOUND));
    }

    @Transactional
    public GroupMemberResponseDto declineInvitation(User user, Long groupId) {
        return groupMemberRepository.declineInvitation(user.getId(), groupId)
                .map(GroupMemberResponseDto::from)
                .orElseThrow(() -> new MemberInvitationsException(GroupErrorCode.INVITATION_NOT_FOUND));
    }

    private void checkPermission(User inviter, List<GroupMember> members) {
        GroupMember inviterMember = members.stream()
                .filter(m -> inviter.getId().equals(m.getUserId()))
                .findFirst()
                .orElseThrow(() -> new MemberInvitationsException(GroupErrorCode.GROUP_MEMBER_NOT_FOUND));

        if (inviterMember.getStatus() != GroupMemberStatus.JOINED) {
            throw new MemberInvitationsException(GroupErrorCode.NOT_GROUP_MEMBER_CANNOT_INVITE);
        }
    }

    private void checkSelfInvite(User inviter, List<GroupMemberRequestDto> invitations) {
        Optional<GroupMemberRequestDto> selfInvite = invitations.stream().filter(x -> x.getInviteEmail().equals(inviter.getEmail())).findFirst();

        if(selfInvite.isPresent()) {
            throw new MemberInvitationsException(GroupErrorCode.CANNOT_INVITE_SELF);
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
            throw new MemberInvitationsException(GroupErrorCode.ALREADY_INVITED_USER);
        }
    }
}
