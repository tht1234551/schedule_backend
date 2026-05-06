package com.jhj.schedule.auth.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class InvalidTokenException extends CustomRuntimeException {
    public InvalidTokenException() {
        super(AuthErrorCode.INVALID_TOKEN);
    }

    public InvalidTokenException(Throwable cause) {
        super(AuthErrorCode.INVALID_TOKEN, cause);
    }
}
