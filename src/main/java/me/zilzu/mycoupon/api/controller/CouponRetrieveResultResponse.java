package me.zilzu.mycoupon.api.controller;


import me.zilzu.mycoupon.application.service.Coupon;

public class CouponRetrieveResultResponse {

    public Coupon coupon;

    public CouponRetrieveResultResponse(Coupon coupon) {
        this.coupon = coupon;
    }
}
