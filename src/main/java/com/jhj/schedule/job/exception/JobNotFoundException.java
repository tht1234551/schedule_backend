package com.jhj.schedule.job.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class JobNotFoundException extends CustomRuntimeException {
    public JobNotFoundException() {
        super(JobErrorCode.JOB_NOT_FOUND);
    }
}