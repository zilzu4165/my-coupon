package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;

public class CouponRequestDto {
    public final CouponDuration duration;
    @JsonProperty("duration_in_months")
    public final Integer durationInMonths;
    @JsonProperty("discount_type")
    public final DiscountType discountType;
    @JsonProperty("currency")
    public final Currency currency;
    @JsonProperty("amount_off")
    public final Long amountOff;
    @JsonProperty("percent_off")
    public final Double percentOff;

    public CouponRequestDto(CouponDuration duration, Integer durationInMonths, DiscountType discountType, Currency currency, Long amountOff, Double percentOff) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
        this.discountType = discountType;
        this.currency = currency;
        this.amountOff = amountOff;
        this.percentOff = percentOff;
    }
}
