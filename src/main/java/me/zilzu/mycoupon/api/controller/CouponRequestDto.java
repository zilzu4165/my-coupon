package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CouponRequestDto {
    public final String duration;
    @JsonProperty("duration_in_months")
    public final Integer durationInMonths;

    public CouponRequestDto(String duration, Integer durationInMonths) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
    }
}
