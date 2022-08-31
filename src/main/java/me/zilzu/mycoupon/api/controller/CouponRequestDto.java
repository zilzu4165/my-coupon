package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.DiscountType;

public class CouponRequestDto {
    public final CouponDuration duration;
    @JsonProperty("duration_in_months")
    public final Integer durationInMonths;
    @JsonProperty("discount_type")
    public final DiscountType discountType;


    public CouponRequestDto(CouponDuration duration, Integer durationInMonths, DiscountType discountType) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
        this.discountType = discountType;
    }
}
