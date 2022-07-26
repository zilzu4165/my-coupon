package me.zilzu.mycoupon.api.controller;


import me.zilzu.mycoupon.application.service.Coupon;

public class CouponRetrieveResultResponse {

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


//    public CouponRetrieveResultResponse(String id, String object, Integer amountOff, long created, String currency, String duration, Integer durationInMonths, Boolean livemode, Integer maxRedemptions, String name, Float percentOff, Boolean valid) {
//        this.id = id;
//        this.object = object;
//        this.amountOff = amountOff;
//        this.created = created;
//        this.currency = currency;
//        this.duration = duration;
//        this.durationInMonths = durationInMonths;
//        this.livemode = livemode;
//        this.maxRedemptions = maxRedemptions;
//        this.name = name;
//        this.percentOff = percentOff;
//        this.valid = valid;
//    }

    public CouponRetrieveResultResponse(Coupon coupon) {
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
