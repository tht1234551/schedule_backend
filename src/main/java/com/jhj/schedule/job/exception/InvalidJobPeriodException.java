package com.jhj.schedule.job.exception;

import com.jhj.schedule.common.exception.CustomRuntimeException;

public class InvalidJobPeriodException extends CustomRuntimeException {
    public InvalidJobPeriodException() {
        super(JobErrorCode.INVALID_JOB_PERIOD);
    }
}