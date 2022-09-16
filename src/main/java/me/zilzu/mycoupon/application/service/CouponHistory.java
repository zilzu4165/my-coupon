package me.zilzu.mycoupon.application.service;

import java.time.LocalDateTime;

public class CouponHistory {

    public final String id;
    public final LocalDateTime usageTime;

    public CouponHistory(String id, LocalDateTime usageTime) {
        this.id = id;
        this.usageTime = usageTime;
    }
}
