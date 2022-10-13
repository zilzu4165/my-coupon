package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.CouponValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exceptionCheck(RuntimeException runtimeException) {
        System.out.println(runtimeException.getMessage());
        return new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), runtimeException.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionCheck(IllegalArgumentException illegalArgumentException) {
        System.out.println(illegalArgumentException.getMessage());
        return new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), illegalArgumentException.getMessage());
    }

    @ExceptionHandler(CouponValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse exceptionCheck() {
        final String ERROR = "INVALID_COUPON_CREATION_REQUEST";
        final String MESSAGE = "쿠폰 생성 규칙에 알맞지 않습니다.";
        return new ExceptionResponse(ERROR, MESSAGE);
    }
}
