package me.zilzu.mycoupon.application.service;

import java.time.LocalDateTime;

public class Coupon {
    public String id;
    public String object;
    public Integer amountOff;
    public CouponCurrency couponCurrency;
    public String duration;
    public Integer durationInMonths;
    public Boolean livemode;
    public Integer maxRedemptions;
    public String name;
    public Float percentOff;
    public Boolean valid;
    public LocalDateTime createdTime;
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public Coupon(String id, String object, Integer amountOff, CouponCurrency couponCurrency, String duration, Integer durationInMonths, Boolean livemode, Integer maxRedemptions, String name, Float percentOff, Boolean valid, LocalDateTime createdTime) {
        this.id = id;
        this.object = object;
        this.amountOff = amountOff;
        this.couponCurrency = couponCurrency;
        this.duration = duration;
        this.durationInMonths = durationInMonths;
        this.livemode = livemode;
        this.maxRedemptions = maxRedemptions;
        this.name = name;
        this.percentOff = percentOff;
        this.valid = valid;
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
