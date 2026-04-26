package com.jhj.schedule.group.application;

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

@Service
@RequiredArgsConstructor
public class GroupInvitationService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;


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
}
