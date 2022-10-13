package me.zilzu.mycoupon.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"me.zilzu.mycoupon.api"})
public class CouponExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CouponExceptionResponse> handleRuntimeException(RuntimeException exception) {
        final String DEFAULT_ERROR = "INTERNAL_ERROR";
        final String DEFAULT_MESSAGE = "알 수 없는 서버 오류가 발생했습니다.";

        CouponExceptionResponse couponExceptionResponse = new CouponExceptionResponse(DEFAULT_ERROR, DEFAULT_MESSAGE);

        if (exception instanceof IllegalArgumentException) {
            couponExceptionResponse = new CouponExceptionResponse("INVALID_COUPON_CREATION_REQUEST", "쿠폰 생성 규칙에 알맞지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(couponExceptionResponse);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(couponExceptionResponse);
    }
}
