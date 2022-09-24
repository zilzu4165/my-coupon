package me.zilzu.mycoupon.application.service;

import java.time.LocalDateTime;

public class CouponHistory {

    public final String id;
    public final String refCouponId;
    public final LocalDateTime usageTime;

    public CouponHistory(String id, String refCouponId, LocalDateTime usageTime) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
    }
}
