package com.jhj.schedule.group.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class GroupAccessDeniedException extends CustomRuntimeException {
    public GroupAccessDeniedException(GroupErrorCode groupErrorCode) {
        super(groupErrorCode);
    }
}
