package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponHistory {

    public final String id;
    public final CouponId refCouponId;
    public final LocalDateTime usageTime;
    public final Currency currency;
    public final Double price;
    public final BigDecimal discountedPrice;

    public CouponHistory(String id, CouponId refCouponId, LocalDateTime usageTime, Currency currency, Double price, BigDecimal discountedPrice) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
        this.currency = currency;
        this.price = price;
        this.discountedPrice = discountedPrice;
    }
}
