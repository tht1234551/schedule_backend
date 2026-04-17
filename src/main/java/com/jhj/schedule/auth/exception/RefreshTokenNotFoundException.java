package com.jhj.schedule.auth.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super("토큰이 존재하지 않습니다");
    }
}