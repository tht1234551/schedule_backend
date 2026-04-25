package com.jhj.schedule.auth.userdetail;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomUserDetail extends User {
    private final com.jhj.schedule.user.User user;

    public CustomUserDetail(com.jhj.schedule.user.User user) {
        super(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getAuthority().name()))
        );

        this.user = user;
    }
}
