package com.jhj.schedule.common.exception;

import lombok.Getter;

@Getter
public class CustomRuntimeException extends RuntimeException {

    private final BaseErrorCode baseErrorCode;

    public CustomRuntimeException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode.getMessage());
        this.baseErrorCode = baseErrorCode;
    }

    public CustomRuntimeException(BaseErrorCode baseErrorCode, Throwable cause) {
        super(baseErrorCode.getMessage(), cause);
        this.baseErrorCode = baseErrorCode;
    }

}
