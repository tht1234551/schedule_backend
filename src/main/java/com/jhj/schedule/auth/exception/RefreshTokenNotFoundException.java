package com.jhj.schedule.auth.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class RefreshTokenNotFoundException extends CustomRuntimeException {
    public RefreshTokenNotFoundException() {
        super(AuthErrorCode.NOT_FOUND_TOKEN);
    }
}