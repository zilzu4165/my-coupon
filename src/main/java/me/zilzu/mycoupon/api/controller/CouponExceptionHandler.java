package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.CouponCreationValidationException;
import me.zilzu.mycoupon.application.service.CouponIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = {"me.zilzu.mycoupon.api"})
public class CouponExceptionHandler extends ResponseEntityExceptionHandler { // Exception Handling Consistency

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CouponExceptionResponse> handleRuntimeException(RuntimeException runtimeException) {
        final String error = "INTERNAL_ERROR";
        final String message = "알 수 없는 서버 오류가 발생했습니다.";

        CouponExceptionResponse couponExceptionResponse = new CouponExceptionResponse(error, message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(couponExceptionResponse);
    }

    @ExceptionHandler(CouponCreationValidationException.class)
    public ResponseEntity<CouponExceptionResponse> handleCouponCreationValidationException(CouponCreationValidationException couponCreationValidationException) {
        final String error = "INVALID_COUPON_CREATION_REQUEST";
        final String message = "쿠폰 생성 규칙에 알맞지 않습니다.";

        CouponExceptionResponse couponExceptionResponse = new CouponExceptionResponse(error, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(couponExceptionResponse);
    }

    @ExceptionHandler(CouponIdNotFoundException.class)
    public ResponseEntity<CouponExceptionResponse> handleCouponIdNotFoundException(CouponIdNotFoundException couponIdNotFoundException) {
        final String error = "NOT_FOUND";
        final String message = "ID 가 " + couponIdNotFoundException.getMessage() + " 인 Coupon 을 찾을 수 없습니다.";

        CouponExceptionResponse couponExceptionResponse = new CouponExceptionResponse(error, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(couponExceptionResponse);
    }
}
