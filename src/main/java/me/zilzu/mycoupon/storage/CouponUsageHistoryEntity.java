package me.zilzu.mycoupon.storage;

import java.time.LocalDateTime;

public class CouponUsageHistoryEntity {

    public String id;

    public String refCouponId;

    public LocalDateTime usageTime;

    public CouponUsageHistoryEntity(String id, String refCouponId, LocalDateTime usageTime) {
        this.id = id;
        this.refCouponId = refCouponId;
        this.usageTime = usageTime;
    }
}
