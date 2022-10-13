package me.zilzu.mycoupon.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = {"me.zilzu.mycoupon.api"})
public class CouponExceptionHandler extends ResponseEntityExceptionHandler { // Exception Handling Consistency

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CouponExceptionResponse> handleRuntimeException(RuntimeException runtimeException) {
        final String DEFAULT_ERROR = "INTERNAL_ERROR";
        final String DEFAULT_MESSAGE = "알 수 없는 서버 오류가 발생했습니다.";

        CouponExceptionResponse couponExceptionResponse = new CouponExceptionResponse(DEFAULT_ERROR, DEFAULT_MESSAGE);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(couponExceptionResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CouponExceptionResponse> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        final String DEFAULT_ERROR = "INVALID_COUPON_CREATION_REQUEST";
        final String DEFAULT_MESSAGE = "쿠폰 생성 규칙에 알맞지 않습니다.";

        CouponExceptionResponse couponExceptionResponse = new CouponExceptionResponse(DEFAULT_ERROR, DEFAULT_MESSAGE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(couponExceptionResponse);
    }
}
