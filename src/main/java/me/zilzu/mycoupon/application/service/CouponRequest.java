package me.zilzu.mycoupon.application.service;

public class CouponRequest {
    public final String duration;

    public final Integer durationInMonths;

    public CouponRequest(String duration, Integer durationInMonths) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
    }


}
