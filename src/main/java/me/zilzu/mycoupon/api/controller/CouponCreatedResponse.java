package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.common.enums.CouponCurrency;

import java.time.LocalDateTime;

public class CouponCreatedResponse {

    public String id;
    public CouponCurrency couponCurrency;
    public String duration;
    public LocalDateTime createdTime;

    public CouponCreatedResponse(Coupon coupon) {
        this.id = coupon.id;
        this.couponCurrency = coupon.couponCurrency;
        this.duration = coupon.duration;
        this.createdTime = coupon.createdTime;
    }
}
