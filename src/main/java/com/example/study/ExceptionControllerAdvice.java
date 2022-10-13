package com.example.study;

import com.example.study.api.payment.common.exception.LFException;
import com.example.study.api.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
class ExceptionControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler({
            NoSuchFieldException.class,
            IllegalStateException.class,
            IllegalArgumentException.class,
            UnsupportedOperationException.class,
            LFException.class})
    public Result<?> handle(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return Result.failure(e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handle(MissingServletRequestParameterException e) {
        LOGGER.error("[{}] 항목은 필수입니다.", e.getParameterName());
        return Result.failure(e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> serverError(MaxUploadSizeExceededException e) {
        LOGGER.error(e.getMessage(), e);
        return Result.failure("파일 당 업로드 할 수 있는 최대 용량은 5MB 입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> serverError(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
