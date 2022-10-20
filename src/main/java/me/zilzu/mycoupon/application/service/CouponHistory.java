package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponCurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponHistory {

    public final String id;
    public final CouponId refCouponId;
    public final LocalDateTime usageTime;
    public final CouponCurrency couponCurrency;
    public final Double price;
    public final BigDecimal discountedPrice;

    public CouponHistory(String id, CouponId refCouponId, LocalDateTime usageTime, CouponCurrency couponCurrency, Double price, BigDecimal discountedPrice) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
        this.couponCurrency = couponCurrency;
        this.price = price;
        this.discountedPrice = discountedPrice;
    }
}
