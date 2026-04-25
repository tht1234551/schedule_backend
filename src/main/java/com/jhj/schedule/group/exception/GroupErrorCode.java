package com.jhj.schedule.group.exception;

import com.jhj.schedule.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements BaseErrorCode {

    GROUP_MEMBER_NOT_FOUND(HttpStatus.FORBIDDEN, "그룹 멤버가 아닙니다."),
    NOT_GROUP_MEMBER_CANNOT_INVITE(HttpStatus.FORBIDDEN, "그룹 멤버가 아닌 사용자는 초대할 수 없습니다."),
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "자기 자신은 초대할 수 없습니다."),
    ALREADY_INVITED_USER(HttpStatus.CONFLICT, "이미 초대된 사용자입니다.");

    private final HttpStatus status;
    private final String message;
}
