package com.jhj.schedule.auth.exception;

import com.jhj.schedule.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀립니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다.");

    private final HttpStatus status;
    private final String message;

}
