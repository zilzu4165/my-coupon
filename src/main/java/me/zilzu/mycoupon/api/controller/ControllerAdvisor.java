package me.zilzu.mycoupon.api.controller;

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


}
