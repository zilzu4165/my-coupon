package me.zilzu.mycoupon.api.controller;


import me.zilzu.mycoupon.application.service.Coupon;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;

import java.time.LocalDateTime;


public class CouponRetrieveResultResponse {

    public final String id;
    public final Currency currency;
    public final CouponDuration duration;
    public final LocalDateTime createdTime;

    public CouponRetrieveResultResponse(Coupon coupon) {
        this.id = coupon.id.value;
        this.currency = coupon.currency;
        this.duration = coupon.duration;
        this.createdTime = coupon.createdTime;
    }

}
