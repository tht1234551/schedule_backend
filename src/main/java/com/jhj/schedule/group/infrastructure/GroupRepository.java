package com.jhj.schedule.group.infrastructure;

import com.jhj.schedule.group.domain.Group;
import com.jhj.schedule.group.domain.GroupMemberStatus;
import com.jhj.schedule.group.dto.response.GroupSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jhj.schedule.jooq.Tables.GROUPS;
import static com.jhj.schedule.jooq.Tables.GROUP_MEMBERS;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

    private final DSLContext dsl;

    public List<GroupSummaryResponseDto> findSummariesByUserId(Long userId) {
        return dsl.select(GROUPS.ID, GROUPS.GROUP_NAME)
                .from(GROUPS)
                .join(GROUP_MEMBERS).on(GROUP_MEMBERS.GROUP_ID.eq(GROUPS.ID))
                .where(
                        GROUP_MEMBERS.USER_ID.eq(userId)
                                .and(GROUP_MEMBERS.STATUS.eq(GroupMemberStatus.JOINED.name()))
                )
                .fetch()
                .map(r -> new GroupSummaryResponseDto(
                        r.get(GROUPS.ID),
                        r.get(GROUPS.GROUP_NAME)
                ));
    }

    public List<GroupSummaryResponseDto> findMyInvitationsGroups(Long userId) {
        return dsl.select(GROUPS.ID, GROUPS.GROUP_NAME)
                .from(GROUPS)
                .join(GROUP_MEMBERS).on(GROUP_MEMBERS.GROUP_ID.eq(GROUPS.ID))
                .where(
                        GROUP_MEMBERS.USER_ID.eq(userId)
                                .and(GROUP_MEMBERS.STATUS.eq(GroupMemberStatus.PENDING.name()))
                )
                .fetch()
                .map(r -> new GroupSummaryResponseDto(
                        r.get(GROUPS.ID),
                        r.get(GROUPS.GROUP_NAME)
                ));
    }



    public Optional<Group> findById(Long id) {
        return dsl.selectFrom(GROUPS)
                .where(GROUPS.ID.eq(id))
                .fetchOptional()
                .map(r -> Group.builder()
                        .id(r.get(GROUPS.ID))
                        .groupName(r.get(GROUPS.GROUP_NAME))
                        .createdAt(r.get(GROUPS.CREATED_AT))
                        .updatedAt(r.get(GROUPS.UPDATED_AT))
                        .build());
    }

    public Group insert(Group group) {
        LocalDateTime now = LocalDateTime.now();
        Long id = dsl.insertInto(GROUPS)
                .set(GROUPS.GROUP_NAME, group.getGroupName())
                .set(GROUPS.CREATED_AT, now)
                .set(GROUPS.UPDATED_AT, now)
                .returningResult(GROUPS.ID)
                .fetchOne()
                .value1();
        group.setId(id);
        group.setCreatedAt(now);
        group.setUpdatedAt(now);

        return group;
    }

    public Group update(Group group) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(GROUPS)
                .set(GROUPS.GROUP_NAME, group.getGroupName())
                .set(GROUPS.UPDATED_AT, now)
                .where(GROUPS.ID.eq(group.getId()))
                .execute();
        group.setUpdatedAt(now);

        return group;
    }

    public void deleteById(Long id) {
        dsl.deleteFrom(GROUPS).where(GROUPS.ID.eq(id)).execute();
    }
}