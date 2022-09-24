package me.zilzu.mycoupon.application.service;

import java.time.LocalDateTime;

public class CouponApplicationResult {
    public final String couponId;
    public final LocalDateTime usageTime;

    public CouponApplicationResult(String couponId, LocalDateTime usageTime) {
        this.couponId = couponId;
        this.usageTime = usageTime;
    }
}
