package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponCurrency;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id) && Objects.equals(duration, coupon.duration) && couponCurrency == coupon.couponCurrency && Objects.equals(createdTime, coupon.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, duration, couponCurrency, createdTime);
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id='" + id + '\'' +
                ", duration='" + duration + '\'' +
                ", couponCurrency=" + couponCurrency +
                ", createdTime=" + createdTime +
                '}';
    }
}
