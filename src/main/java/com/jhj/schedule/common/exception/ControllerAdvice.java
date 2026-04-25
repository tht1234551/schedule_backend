package com.jhj.schedule.common.exception;

import com.jhj.schedule.auth.exception.EmailAlreadyExistsException;
import com.jhj.schedule.auth.exception.RefreshTokenNotFoundException;
import com.jhj.schedule.job.exception.InvalidJobPeriodException;
import com.jhj.schedule.job.exception.JobNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailDup(EmailAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<String> handleRefreshTokenNotFound(RefreshTokenNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(InvalidJobPeriodException.class)
    public ResponseEntity<String> handleInvalidJobPeriod(InvalidJobPeriodException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<String> handleJobNotFound(JobNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomRuntimeException e) {
        HttpStatus status = e.getBaseErrorCode().getStatus();
        String message = e.getMessage();

        ErrorResponse response = ErrorResponse.builder(e, status, message)
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "서버 오류가 발생했습니다.";

        ErrorResponse response = ErrorResponse.builder(e, status, message)
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
