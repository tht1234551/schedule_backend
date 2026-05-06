package com.jhj.schedule.user.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class UserNotFoundException extends CustomRuntimeException {

    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(UserErrorCode userErrorCode) {
        super(userErrorCode);
    }

    public UserNotFoundException(UserErrorCode userErrorCode, Throwable cause) {
        super(userErrorCode, cause);
    }

}
