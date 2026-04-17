package com.jhj.schedule.job.exception;

public class InvalidJobPeriodException extends RuntimeException {
    public InvalidJobPeriodException() {
        super("종료일은 시작일 이후여야 합니다.");
    }
}