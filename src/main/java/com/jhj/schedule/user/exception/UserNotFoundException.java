package com.jhj.schedule.user.exception;

import com.jhj.schedule.common.exception.BaseErrorCode;
import com.jhj.schedule.common.exception.CustomRuntimeException;

public class UserNotFoundException extends CustomRuntimeException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
