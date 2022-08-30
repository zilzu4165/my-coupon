package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.common.enums.CouponDuration;

public class CouponRequestDto {
    public final CouponDuration duration;
    @JsonProperty("duration_in_months")
    public final Integer durationInMonths;

    public CouponRequestDto(CouponDuration duration, Integer durationInMonths) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
    }
}
