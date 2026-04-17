package com.jhj.schedule.group;

import com.jhj.schedule.group.dto.GroupSummaryResponseDto;
import com.jhj.schedule.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    @Query("""
        SELECT new com.jhj.schedule.group.dto.GroupSummaryResponseDto(g.id, g.groupName)
        FROM GroupEntity g JOIN g.members m
        WHERE m.user = :user
    """)
    List<GroupSummaryResponseDto> findSummariesByUser(@Param("user") UserEntity user);
}
