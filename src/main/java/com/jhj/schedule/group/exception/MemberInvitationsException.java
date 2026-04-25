package com.jhj.schedule.group.exception;

import com.jhj.schedule.common.exception.BaseErrorCode;
import com.jhj.schedule.common.exception.CustomRuntimeException;

public class MemberInvitationsException extends CustomRuntimeException {
    public MemberInvitationsException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode);
    }
}
