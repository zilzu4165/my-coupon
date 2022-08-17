package me.zilzu.mycoupon.api.controller;


import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.application.service.CouponCurrency;

import java.time.LocalDateTime;


public class CouponRetrieveResultResponse {

    public final String id;
    public final CouponCurrency couponCurrency;
    public final String duration;
    public final LocalDateTime createdTime;

    public CouponRetrieveResultResponse(Coupon coupon) {
        this.id = coupon.id;
        this.couponCurrency = coupon.couponCurrency;
        this.duration = coupon.duration;
        this.createdTime = coupon.createdTime;
    }

}
