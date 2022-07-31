package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import me.zilzu.mycoupon.application.service.Coupon;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CouponCreatedResponse {

    public String id;
    public String object;
    public Integer amountOff;
    public long created;
    public String currency;
    public String duration;
    public Integer durationInMonths;
    public Boolean livemode;
    public Integer maxRedemptions;
    public String name;
    public Float percentOff;
    public Boolean valid;

    public CouponCreatedResponse(Coupon coupon) {
        this.id = coupon.id;
        this.object = coupon.object;
        this.amountOff = coupon.amountOff;
        this.created = coupon.created;
        this.currency = coupon.currency;
        this.duration = coupon.duration;
        this.durationInMonths = coupon.durationInMonths;
        this.livemode = coupon.livemode;
        this.maxRedemptions = coupon.maxRedemptions;
        this.name = coupon.name;
        this.percentOff = coupon.percentOff;
        this.valid = coupon.valid;
    }
}
