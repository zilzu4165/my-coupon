package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;

public class CouponStatsResult {

    public BigDecimal sum;
    public BigDecimal average;
    public Currency currency;

    public CouponStatsResult(BigDecimal sum, BigDecimal average, Currency currency) {
        this.sum = sum;
        this.average = average;
        this.currency = currency;
    }
}
