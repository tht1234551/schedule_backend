package com.jhj.schedule.auth.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class ExpiredTokenException extends CustomRuntimeException {
    public ExpiredTokenException() {
        super(AuthErrorCode.EXPIRED_TOKEN);
    }

    public ExpiredTokenException(Throwable cause) {
        super(AuthErrorCode.EXPIRED_TOKEN, cause);
    }
}
