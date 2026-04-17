package com.jhj.schedule.job;


import com.jhj.schedule.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    Optional<JobEntity> findByIdAndUser(Long id, UserEntity user);

    @Query("SELECT j FROM JobEntity j WHERE j.user = :user " +
            "AND j.startDate < :monthEnd AND j.endDate >= :monthStart")
    List<JobEntity> findOverlappingJobs(@Param("user") UserEntity user,
                                        @Param("monthStart") LocalDateTime monthStart,
                                        @Param("monthEnd") LocalDateTime monthEnd);
}
