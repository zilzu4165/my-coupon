package me.zilzu.mycoupon.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RateOfDate {
    public final LocalDate date;
    public final BigDecimal rate;

    public RateOfDate(LocalDate date, BigDecimal rate) {
        this.date = date;
        this.rate = rate;
    }
}
