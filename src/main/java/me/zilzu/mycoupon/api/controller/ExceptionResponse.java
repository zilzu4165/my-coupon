package me.zilzu.mycoupon.api.controller;

public class ExceptionResponse {
    public final String error;
    public final String message;

    public ExceptionResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
