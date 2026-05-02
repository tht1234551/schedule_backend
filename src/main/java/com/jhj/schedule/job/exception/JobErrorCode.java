package com.jhj.schedule.job.exception;

import com.jhj.schedule.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JobErrorCode implements BaseErrorCode {

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."),
    INVALID_JOB_PERIOD(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
