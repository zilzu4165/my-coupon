package me.zilzu.mycoupon.storage;


import me.zilzu.mycoupon.common.enums.CouponCurrency;

import java.time.LocalDateTime;

public class CouponEntity {
    public final String id;
    public final String duration;
    public final CouponCurrency couponCurrency;
    public final LocalDateTime createdTime;


    public CouponEntity(String id, String duration, CouponCurrency couponCurrency, LocalDateTime createdTime) {
        this.id = id;
        this.duration = duration;
        this.couponCurrency = couponCurrency;
        this.createdTime = createdTime;
    }
}
