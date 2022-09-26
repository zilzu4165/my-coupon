package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;

import java.time.LocalDateTime;

public class CouponApplicationResult {
    public final CouponId couponId;
    public final LocalDateTime usageTime;

    public CouponApplicationResult(CouponId couponId, LocalDateTime usageTime) {
        this.couponId = couponId;
        this.usageTime = usageTime;
    }
}
