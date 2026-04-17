package com.jhj.schedule.job.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException() {
        super("일정을 찾을 수 없습니다.");
    }
}