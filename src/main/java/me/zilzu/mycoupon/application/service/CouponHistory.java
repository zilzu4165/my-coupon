package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;

import java.time.LocalDateTime;

public class CouponHistory {

    public final String id;
    public final CouponId refCouponId;
    public final LocalDateTime usageTime;
    public final Double price;
    public final Double discountedPrice;

    public CouponHistory(String id, CouponId refCouponId, LocalDateTime usageTime, Double price, Double discountedPrice) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
        this.price = price;
        this.discountedPrice = discountedPrice;
    }
}
