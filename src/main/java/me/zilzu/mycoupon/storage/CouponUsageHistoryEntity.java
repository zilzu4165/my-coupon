package me.zilzu.mycoupon.storage;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class CouponUsageHistoryEntity {

    @Id
    public String id;

    public String refCouponId;

    public LocalDateTime usageTime;

    public Double price;

    public Double discountedPrice;

    public CouponUsageHistoryEntity() {

    }

    public CouponUsageHistoryEntity(String id, String refCouponId, LocalDateTime usageTime) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
    }

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
