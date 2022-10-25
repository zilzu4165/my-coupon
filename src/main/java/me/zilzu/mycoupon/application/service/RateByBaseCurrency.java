package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


public class RateByBaseCurrency {
    public LocalDate date;
    public Currency base;
    public Map<Currency, BigDecimal> rates;

    @Override
    public String toString() {
        return "RateByBaseCurrency{" +
                "date=" + date +
                ", base=" + base +
                ", rates=" + rates +
                '}';
    }
}
