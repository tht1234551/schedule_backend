package com.jhj.schedule.group.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;
import com.jhj.schedule.common.exception.ErrorCode;

public class MemberInvitationsException extends CustomRuntimeException {
    public MemberInvitationsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
