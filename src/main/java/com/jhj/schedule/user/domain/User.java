package com.jhj.schedule.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String avatar;

    @Builder.Default
    private Authority authority = Authority.ROLE_USER;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}