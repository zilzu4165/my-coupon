package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;

public class CouponRequest {
    public final CouponDuration duration;

    public final Integer durationInMonths;

    public CouponRequest(CouponDuration duration, Integer durationInMonths) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
    }


}
