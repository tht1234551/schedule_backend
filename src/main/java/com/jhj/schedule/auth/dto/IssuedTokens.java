package com.jhj.schedule.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IssuedTokens {
    private String accessToken;
    private String refreshToken;
}