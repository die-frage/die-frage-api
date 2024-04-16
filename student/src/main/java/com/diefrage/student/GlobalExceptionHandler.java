package com.diefrage.student;

import com.diefrage.exceptions.ServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static class ExceptionWrapper {
        public String code;
        public Integer status;
        public String message;

        public ExceptionWrapper(ServerException e) {
            this.code = e.getCode();
            this.status = e.getStatus().value();
            this.message = e.getMessage();
        }
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionWrapper> catchServerException(ServerException e) {
        return new ResponseEntity<>(new ExceptionWrapper(e), e.getStatus());
    }
}
