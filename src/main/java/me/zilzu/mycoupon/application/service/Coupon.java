package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Coupon implements Serializable {

    public final CouponId id;
    public final CouponDuration duration;
    public final Integer durationInMonth;
    public final Currency currency;
    public final DiscountType discountType;
    public final Long amountOff;
    public final Double percentOff;
    public final Boolean valid;
    public final LocalDateTime createdTime;

    public Coupon(CouponId id, CouponDuration duration, Integer durationInMonth, Currency currency, DiscountType discountType, Long amountOff, Double percentOff, Boolean valid, LocalDateTime createdTime) {
        this.id = id;
        this.duration = duration;
        this.durationInMonth = durationInMonth;
        this.currency = currency;
        this.discountType = discountType;
        this.amountOff = amountOff;
        this.percentOff = percentOff;
        this.valid = valid;
        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coupon coupon = (Coupon) o;

        if (!Objects.equals(id, coupon.id)) return false;
        if (!Objects.equals(duration, coupon.duration)) return false;
        if (currency != coupon.currency) return false;
        return Objects.equals(createdTime, coupon.createdTime);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (createdTime != null ? createdTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Coupon{" + "id='" + id + '\'' + ", duration='" + duration + '\'' + ", couponCurrency=" + currency + ", createdTime=" + createdTime + '}';
    }
}
