package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponCurrency;

import java.time.LocalDateTime;

public class Coupon {
    public final String id;
    public final String duration;
    public final CouponCurrency couponCurrency;
    public final LocalDateTime createdTime;


    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public Coupon(String id, String duration, CouponCurrency couponCurrency, LocalDateTime createdTime) {
        this.id = id;
        this.duration = duration;
        this.couponCurrency = couponCurrency;
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id='" + id + '\'' +
                ", date=" + createdTime +
                '}';
    }
}
