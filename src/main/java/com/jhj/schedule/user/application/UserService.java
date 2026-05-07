package com.jhj.schedule.user.application;

import com.jhj.schedule.user.exception.EmailAlreadyExistsException;
import com.jhj.schedule.common.config.RedisCacheConfig;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.exception.UserNotFoundException;
import com.jhj.schedule.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 로그인 전용
     * 캐시안함(비밀번호 존재함)
     */
    public User loadCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Cacheable(value = RedisCacheConfig.USER_BY_ID, key = "#id")
    public User findById(Long id) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setPassword(null);
                    return u;
                })
                .orElseThrow(UserNotFoundException::new);
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        return userRepository.insert(user);
    }

    // TODO: user 기본 기능 개발

    @CacheEvict(value = RedisCacheConfig.USER_BY_ID, key = "#user.id")
    public User update(User user) {
        return null;
    }

    @CacheEvict(value = RedisCacheConfig.USER_BY_ID, key = "#user.id")
    public void delete(User user) {
        return;
    }
}