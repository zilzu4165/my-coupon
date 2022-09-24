package me.zilzu.mycoupon.api.controller;


import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;

import java.time.LocalDateTime;


public class CouponRetrieveResultResponse {

    public final String id;
    public final CouponCurrency couponCurrency;
    public final CouponDuration duration;
    public final LocalDateTime createdTime;

    public CouponRetrieveResultResponse(Coupon coupon) {
        this.id = coupon.id.value;
        this.couponCurrency = coupon.couponCurrency;
        this.duration = coupon.duration;
        this.createdTime = coupon.createdTime;
    }

}
