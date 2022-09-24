package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;

import java.time.LocalDateTime;

public class CouponHistory {

    public final String id;
    public final CouponId refCouponId;
    public final LocalDateTime usageTime;

    public CouponHistory(String id, CouponId refCouponId, LocalDateTime usageTime) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
    }
}
