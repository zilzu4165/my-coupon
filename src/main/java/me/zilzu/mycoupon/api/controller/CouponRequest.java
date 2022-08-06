package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CouponRequest {
    private final String duration;
    @JsonProperty("duration_in_months")
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
