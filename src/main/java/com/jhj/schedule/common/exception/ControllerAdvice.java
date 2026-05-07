package com.jhj.schedule.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<ProblemDetail> handleCustomException(CustomRuntimeException e) {
        log.error("Error", e);

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
        log.error("Error", e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "서버 오류가 발생했습니다.";

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setDetail(message);

        return ResponseEntity
                .status(status)
                .body(problem);
    }
}
