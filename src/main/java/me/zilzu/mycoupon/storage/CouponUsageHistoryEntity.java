package me.zilzu.mycoupon.storage;

import java.time.LocalDateTime;

public class CouponUsageHistoryEntity {

    public String id;
    //    public Integer usageCount;
    public LocalDateTime usageTime;

    public CouponUsageHistoryEntity(String id, LocalDateTime usageTime) {
        this.id = id;
        this.usageTime = usageTime;
    }
}
