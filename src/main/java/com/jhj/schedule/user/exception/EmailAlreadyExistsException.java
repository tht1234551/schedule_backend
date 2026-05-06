package com.jhj.schedule.user.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class EmailAlreadyExistsException extends CustomRuntimeException {
    public EmailAlreadyExistsException() {
        super(UserErrorCode.EMAIL_ALREADY_EXISTS);
    }
}
