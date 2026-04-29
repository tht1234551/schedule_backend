package com.jhj.schedule.common.exception;

import com.jhj.schedule.auth.exception.EmailAlreadyExistsException;
import com.jhj.schedule.auth.exception.RefreshTokenNotFoundException;
import com.jhj.schedule.common.util.ActiveProfileUtil;
import com.jhj.schedule.job.exception.InvalidJobPeriodException;
import com.jhj.schedule.job.exception.JobNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final ActiveProfileUtil activeProfileUtil;

    // TODO: 정리

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
    public ResponseEntity<ProblemDetail> handleCustomException(CustomRuntimeException e) {
        HttpStatus status = e.getBaseErrorCode().getStatus();
        String message = e.getMessage();

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setDetail(message);

        return ResponseEntity
                .status(status)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception e) {
        if (activeProfileUtil.isNotProd()) {
            e.printStackTrace();
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "서버 오류가 발생했습니다.";

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setDetail(message);

        return ResponseEntity
                .status(status)
                .body(problem);
    }
}
