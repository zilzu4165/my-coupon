package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;

public class CouponRequest {
    public final CouponDuration duration;
    public final Integer durationInMonths;
    public final DiscountType discountType;
    public final Long amountOff;
    public final Float percentOff;

    public CouponRequest(CouponDuration duration, Integer durationInMonths, DiscountType discountType, Long amountOff, Float percentOff) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
        this.discountType = discountType;
        this.amountOff = amountOff;
        this.percentOff = percentOff;
    }


}
