package com.jhj.schedule.group.infrastructure;

import com.jhj.schedule.group.domain.GroupMember;
import com.jhj.schedule.group.domain.GroupMemberStatus;
import com.jhj.schedule.group.domain.GroupRole;
import com.jhj.schedule.group.dto.request.GroupMemberRequestDto;
import com.jhj.schedule.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jhj.schedule.jooq.Tables.*;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepository {

    private final DSLContext dsl;

    public GroupMember insert(GroupMember member) {
        LocalDateTime now = LocalDateTime.now();

        Long id = dsl.insertInto(GROUP_MEMBERS)
                .set(GROUP_MEMBERS.GROUP_ID, member.getGroupId())
                .set(GROUP_MEMBERS.USER_ID, member.getUserId())
                .set(GROUP_MEMBERS.ROLE,
                        Optional.ofNullable(member.getRole())
                                .orElse(GroupRole.MEMBER)
                                .name()
                )
                .set(GROUP_MEMBERS.INVITE_EMAIL, member.getInviteEmail())
                .set(GROUP_MEMBERS.STATUS,
                        Optional.ofNullable(member.getStatus())
                                .orElse(GroupMemberStatus.PENDING)
                                .name()
                )
                .set(GROUP_MEMBERS.USER_NAME, member.getUserName())
                .set(GROUP_MEMBERS.CREATED_AT, now)
                .set(GROUP_MEMBERS.UPDATED_AT, now)
                .returningResult(GROUP_MEMBERS.ID)
                .fetchOne()
                .value1();

        member.setId(id);
        member.setCreatedAt(now);
        member.setUpdatedAt(now);

        return member;
    }

    public List<GroupMember> insertAll(List<GroupMember> members) {
        if (members.isEmpty()) {
            return members;
        }

        LocalDateTime now = LocalDateTime.now();

        var step = dsl.insertInto(GROUP_MEMBERS,
                GROUP_MEMBERS.GROUP_ID,
                GROUP_MEMBERS.USER_ID,
                GROUP_MEMBERS.USER_NAME,
                GROUP_MEMBERS.ROLE,
                GROUP_MEMBERS.INVITE_EMAIL,
                GROUP_MEMBERS.STATUS,
                GROUP_MEMBERS.CREATED_AT,
                GROUP_MEMBERS.UPDATED_AT);

        for (GroupMember member : members) {
            step = step.values(
                    member.getGroupId(),
                    member.getUserId(),
                    member.getUserName(),
                    Optional.ofNullable(member.getRole())
                            .orElse(GroupRole.MEMBER)
                            .name(),
                    member.getInviteEmail(),
                    Optional.ofNullable(member.getStatus())
                            .orElse(GroupMemberStatus.PENDING)
                            .name(),
                    now,
                    now);
        }

        List<Long> ids = step.returningResult(GROUP_MEMBERS.ID)
                .fetch()
                .map(r -> r.value1());

        for (int i = 0; i < members.size(); i++) {
            GroupMember member = members.get(i);
            member.setId(ids.get(i));
            member.setCreatedAt(now);
            member.setUpdatedAt(now);
        }

        return members;
    }

    public GroupMember update(GroupMember member) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(GROUP_MEMBERS)
                .set(GROUP_MEMBERS.USER_NAME, member.getUserName())
                .set(GROUP_MEMBERS.ROLE,
                        Optional.ofNullable(member.getRole())
                                .orElse(GroupRole.MEMBER)
                                .name()
                )
                .set(GROUP_MEMBERS.INVITE_EMAIL, member.getInviteEmail())
                .set(GROUP_MEMBERS.STATUS,
                        Optional.ofNullable(member.getStatus())
                                .orElse(GroupMemberStatus.PENDING)
                                .name()
                )
                .set(GROUP_MEMBERS.UPDATED_AT, now)
                .where(GROUP_MEMBERS.ID.eq(member.getId()))
                .execute();
        member.setUpdatedAt(now);

        return member;
    }

    public GroupMember invite(User inviter, Long groupId, List<GroupMemberRequestDto> invitations) {
        LocalDateTime now = LocalDateTime.now();
//
//        dsl.update(GROUP_MEMBERS)
//                .set(GROUP_MEMBERS.USER_NAME, member.getUserName())
//                .set(GROUP_MEMBERS.ROLE,
//                        Optional.ofNullable(member.getRole())
//                                .orElse(GroupRole.MEMBER)
//                                .name()
//                )
//                .set(GROUP_MEMBERS.INVITE_EMAIL, member.getInviteEmail())
//                .set(GROUP_MEMBERS.STATUS,
//                        Optional.ofNullable(member.getStatus())
//                                .orElse(GroupMemberStatus.PENDING)
//                                .name()
//                )
//                .set(GROUP_MEMBERS.UPDATED_AT, now)
//                .where(GROUP_MEMBERS.ID.eq(member.getId()))
//                .execute();
//        member.setUpdatedAt(now);
//
//        return member;
        return null;
    }

    public List<GroupMember> findByGroupId(Long groupId) {
        return dsl.select(
                    GROUP_MEMBERS.ID,
                    GROUP_MEMBERS.GROUP_ID,
                    GROUP_MEMBERS.USER_ID,
                    GROUP_MEMBERS.USER_NAME,
                    GROUP_MEMBERS.ROLE,
                    GROUP_MEMBERS.INVITE_EMAIL,
                    GROUP_MEMBERS.STATUS,
                    GROUP_MEMBERS.CREATED_AT,
                    GROUP_MEMBERS.UPDATED_AT,
                    USERS.EMAIL
                )
                .from(GROUP_MEMBERS)
                .join(USERS).on(GROUP_MEMBERS.USER_ID.eq(USERS.ID))
                .where(GROUP_MEMBERS.GROUP_ID.eq(groupId))
                .fetch()
                .map(r ->
                        GroupMember.builder()
                                .id(r.get(GROUP_MEMBERS.ID))
                                .groupId(r.get(GROUP_MEMBERS.GROUP_ID))
                                .userId(r.get(GROUP_MEMBERS.USER_ID))
                                .userName(r.get(GROUP_MEMBERS.USER_NAME))
                                .role(GroupRole.valueOf(r.get(GROUP_MEMBERS.ROLE)))
                                .inviteEmail(Optional.ofNullable(r.get(GROUP_MEMBERS.INVITE_EMAIL)).orElse(r.get(USERS.EMAIL)))
                                .status(GroupMemberStatus.valueOf(r.get(GROUP_MEMBERS.STATUS)))
                                .createdAt(r.get(GROUP_MEMBERS.CREATED_AT))
                                .updatedAt(r.get(GROUP_MEMBERS.UPDATED_AT))
                                .build()
                );
    }

    public Optional<GroupMember> acceptInvitation(Long userId, Long groupId) {
        LocalDateTime now = LocalDateTime.now();

        return dsl.update(GROUP_MEMBERS)
                .set(GROUP_MEMBERS.STATUS, GroupMemberStatus.JOINED.name())
                .set(GROUP_MEMBERS.UPDATED_AT, now)
                .where(
                        GROUP_MEMBERS.USER_ID.eq(userId)
                                .and(GROUP_MEMBERS.GROUP_ID.eq(groupId))
                                .and(GROUP_MEMBERS.STATUS.eq(GroupMemberStatus.PENDING.name()))
                )
                .returningResult(
                        GROUP_MEMBERS.ID,
                        GROUP_MEMBERS.GROUP_ID,
                        GROUP_MEMBERS.USER_ID,
                        GROUP_MEMBERS.USER_NAME,
                        GROUP_MEMBERS.ROLE,
                        GROUP_MEMBERS.STATUS,
                        GROUP_MEMBERS.UPDATED_AT
                )
                .fetchOptional()
                .map(this::toDomain);
    }

    public Optional<GroupMember> declineInvitation(Long userId, Long groupId) {
        LocalDateTime now = LocalDateTime.now();

        return dsl.update(GROUP_MEMBERS)
                .set(GROUP_MEMBERS.STATUS, GroupMemberStatus.DECLINED.name())
                .set(GROUP_MEMBERS.UPDATED_AT, now)
                .where(
                        GROUP_MEMBERS.USER_ID.eq(userId)
                                .and(GROUP_MEMBERS.GROUP_ID.eq(groupId))
                                .and(GROUP_MEMBERS.STATUS.eq(GroupMemberStatus.PENDING.name()))
                )
                .returningResult(
                        GROUP_MEMBERS.ID,
                        GROUP_MEMBERS.GROUP_ID,
                        GROUP_MEMBERS.USER_ID,
                        GROUP_MEMBERS.USER_NAME,
                        GROUP_MEMBERS.ROLE,
                        GROUP_MEMBERS.STATUS,
                        GROUP_MEMBERS.UPDATED_AT
                )
                .fetchOptional()
                .map(this::toDomain);
    }

    private GroupMember toDomain(Record r) {
        return GroupMember.builder()
                .id(r.get(GROUP_MEMBERS.ID))
                .groupId(r.get(GROUP_MEMBERS.GROUP_ID))
                .userId(r.get(GROUP_MEMBERS.USER_ID))
                .userName(r.get(GROUP_MEMBERS.USER_NAME))
                .role(GroupRole.valueOf(r.get(GROUP_MEMBERS.ROLE)))
                .status(GroupMemberStatus.valueOf(r.get(GROUP_MEMBERS.STATUS)))
                .updatedAt(r.get(GROUP_MEMBERS.UPDATED_AT))
                .build()
        ;
    }
}