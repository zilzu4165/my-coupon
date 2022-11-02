package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CouponRateCalculationResult {

    public CouponId id;
    public BigDecimal calculatedRate;
    public Currency currency;
    public LocalDate date;

    public CouponRateCalculationResult(CouponId id, BigDecimal calculatedRate, Currency currency, LocalDate date) {
        this.id = id;
        this.calculatedRate = calculatedRate;
        this.currency = currency;
        this.date = date;
    }
}
