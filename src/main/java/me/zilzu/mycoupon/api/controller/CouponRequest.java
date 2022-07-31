package me.zilzu.mycoupon.api.controller;

public class CouponRequest {
    private final String duration;
    private final Integer durationInMonths;


    public String getDuration() {
        return duration;
    }

    public Integer getDurationInMonths() {
        return durationInMonths;
    }

    public CouponRequest(String duration, Integer durationInMonths) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
    }
}
