package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.enums.CouponCurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponUsageHistoryEntity {

    public String id;
    public String refCouponId;
    public LocalDateTime usageTime;
    public CouponCurrency couponCurrency;
    public Double price;
    public BigDecimal discountedPrice;

    public CouponUsageHistoryEntity(String id, String refCouponId, LocalDateTime usageTime, CouponCurrency couponCurrency, Double price, BigDecimal discountedPrice) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
        this.couponCurrency = couponCurrency;
        this.price = price;
        this.discountedPrice = discountedPrice;
    }

    @Override
    public String toString() {
        return "CouponUsageHistoryEntity{" +
                "id='" + id + '\'' +
                ", refCouponId='" + refCouponId + '\'' +
                ", usageTime=" + usageTime +
                ", price=" + price +
                ", discountedPrice=" + discountedPrice +
                '}';
    }
}
