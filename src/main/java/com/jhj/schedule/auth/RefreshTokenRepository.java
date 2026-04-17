package com.jhj.schedule.auth;

import com.jhj.schedule.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Transactional
    void deleteByUser(UserEntity user);
    void deleteByToken(String token);
    Optional<RefreshTokenEntity> findByToken(String token);
}
