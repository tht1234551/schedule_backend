package com.jhj.schedule.user.application;

import com.jhj.schedule.auth.exception.EmailAlreadyExistsException;
import com.jhj.schedule.user.infrastructure.UserRepository;
import com.jhj.schedule.user.domain.User;
import com.jhj.schedule.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        return userRepository.insert(user);
    }
}