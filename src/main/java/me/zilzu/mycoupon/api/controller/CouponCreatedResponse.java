package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;

import java.time.LocalDateTime;

public class CouponCreatedResponse {

    public String id;
    public Currency currency;
    public CouponDuration duration;
    public LocalDateTime createdTime;

    public CouponCreatedResponse(Coupon coupon) {
        this.id = coupon.id.value;
        this.currency = coupon.currency;
        this.duration = coupon.duration;
        this.createdTime = coupon.createdTime;
    }
}
