package me.zilzu.mycoupon.api.controller;

import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;

public class CouponRateStatsResponse {

    public BigDecimal sum;
    public BigDecimal average;
    public Currency currency;

    public CouponRateStatsResponse(BigDecimal sum, BigDecimal average, Currency currency) {
        this.sum = sum;
        this.average = average;
        this.currency = currency;
    }
}
