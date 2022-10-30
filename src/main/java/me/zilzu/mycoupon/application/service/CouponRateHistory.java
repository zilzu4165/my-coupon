package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CouponRateHistory implements Serializable {

    public LocalDate date;
    public Currency base;
    public Map<Currency, BigDecimal> rates;

    public CouponRateHistory() {
    }

    public CouponRateHistory(LocalDate date, Currency base, Map<Currency, BigDecimal> rates) {
        this.date = date;
        this.base = base;
        this.rates = rates;
    }


    @Override
    public String toString() {
        return "CouponRateHistory{" +
                "date='" + date + '\'' +
                ", base='" + base + '\'' +
                ", rates=" + rates +
                '}';
    }
}
