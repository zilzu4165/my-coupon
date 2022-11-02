package me.zilzu.mycoupon.application.service;

import java.math.BigDecimal;

public class CouponStatsSummary {
    public final BigDecimal sum;
    public final BigDecimal average;


    public CouponStatsSummary(BigDecimal sum, BigDecimal average) {
        this.sum = sum;
        this.average = average;
    }
}
