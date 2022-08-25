package me.zilzu.mycoupon.storage;


import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;

import java.time.LocalDateTime;

public class CouponEntity {
    public final String id;
    public final CouponDuration duration;
    public final Integer duration_in_month;
    public final CouponCurrency couponCurrency;
    public final LocalDateTime createdTime;

    public CouponEntity(String id, CouponDuration duration, Integer duration_in_month, CouponCurrency couponCurrency, LocalDateTime createdTime) {
        this.id = id;
        this.duration = duration;
        this.duration_in_month = duration_in_month;
        this.couponCurrency = couponCurrency;
        this.createdTime = createdTime;
    }
}
