package me.zilzu.mycoupon.storage;

import java.time.LocalDateTime;

public class CouponUsageHistoryEntity {

    public String id;
    public String refCouponId;
    public LocalDateTime usageTime;
    public Double price;
    public Double discountedPrice;

    public CouponUsageHistoryEntity(String id, String refCouponId, LocalDateTime usageTime, Double price, Double discountedPrice) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
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
