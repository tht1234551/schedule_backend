package com.jhj.schedule.group;

import com.jhj.schedule.group.dto.GroupRequestDto;
import com.jhj.schedule.group.dto.GroupResponseDto;
import com.jhj.schedule.group.dto.GroupSummaryResponseDto;
import com.jhj.schedule.user.UserEntity;
import com.jhj.schedule.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<GroupSummaryResponseDto> findGroups(UserEntity userEntity) {
        return groupRepository.findSummariesByUser(userEntity);
    }

    @Transactional
    public GroupResponseDto createGroup(UserEntity userEntity, GroupRequestDto request) {
        GroupEntity groupEntity = GroupEntity.builder()
                .groupName(request.getGroupName())
                .build();

        GroupMemberEntity ownerMember = GroupMemberEntity.builder()
                .user(userEntity)
                .role(GroupRole.ADMIN)
                .build();

        groupEntity.addMember(ownerMember);

        request.getMembers().forEach(memberDto -> {
            if (memberDto.getUserId().equals(userEntity.getId())) {
                return;
            }

            UserEntity memberUser = userRepository.findById(memberDto.getUserId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            GroupMemberEntity member = GroupMemberEntity.builder()
                    .user(memberUser)
                    .role(GroupRole.MEMBER)
                    .build();

            groupEntity.addMember(member);
        });

        GroupEntity saved = groupRepository.save(groupEntity);
        return GroupResponseDto.from(saved);
    }
}