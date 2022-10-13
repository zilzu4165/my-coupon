package me.zilzu.mycoupon.api.controller;

public class CouponExceptionResponse {
    public final String error;
    public final String message;

    public CouponExceptionResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
