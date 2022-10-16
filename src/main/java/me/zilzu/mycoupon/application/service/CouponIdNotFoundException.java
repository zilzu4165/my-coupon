package me.zilzu.mycoupon.application.service;

public class CouponIdNotFoundException extends IllegalArgumentException {
    public CouponIdNotFoundException(String value) {
        super(value);
    }
}
