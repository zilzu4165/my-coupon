package me.zilzu.mycoupon.storage;


import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponCurrency;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;

import java.time.LocalDateTime;

public class CouponEntity {
    public CouponId id;
    public CouponDuration duration;
    public Integer durationInMonth;
    public CouponCurrency couponCurrency;
    public DiscountType discountType;
    public Long amountOff;
    public Double percentOff;
    public Boolean valid;
    public LocalDateTime createdTime;

    public CouponEntity(CouponId id, CouponDuration duration, Integer durationInMonth, CouponCurrency couponCurrency, DiscountType discountType, Long amountOff, Double percentOff, Boolean valid, LocalDateTime createdTime) {
        this.id = id;
        this.duration = duration;
        this.durationInMonth = durationInMonth;
        this.couponCurrency = couponCurrency;
        this.discountType = discountType;
        this.amountOff = amountOff;
        this.percentOff = percentOff;
        this.valid = valid;
        this.createdTime = createdTime;
    }
}
