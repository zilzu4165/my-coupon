package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.CouponDuration;
import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.common.enums.DiscountType;

public class CouponCreationRequest {
    public final CouponDuration duration;
    public final Integer durationInMonths;
    public final DiscountType discountType;
    public final Currency currency;
    public final Long amountOff;
    public final Double percentOff;

    public CouponCreationRequest(CouponDuration duration, Integer durationInMonths, DiscountType discountType, Currency currency, Long amountOff, Double percentOff) {
        this.duration = duration;
        this.durationInMonths = durationInMonths;
        this.discountType = discountType;
        this.currency = currency;
        this.amountOff = amountOff;
        this.percentOff = percentOff;
    }
}
