package com.jhj.schedule.auth.userdetail;

import com.jhj.schedule.user.UserEntity;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomUserDetail extends User {
    private final UserEntity user;

    public CustomUserDetail(UserEntity user) {
        super(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getAuthority().name()))
        );

        this.user = user;
    }
}
