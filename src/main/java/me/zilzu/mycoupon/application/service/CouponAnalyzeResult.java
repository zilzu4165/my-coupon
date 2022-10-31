package me.zilzu.mycoupon.application.service;

import java.math.BigDecimal;
import java.time.Month;

public class CouponAnalyzeResult {
    public final Integer year;
    public final Month month;
    public final BigDecimal sumAmountsOfCoupon;
    public final BigDecimal averageAmount;

    public CouponAnalyzeResult(Integer year, Month month, BigDecimal sumAmountsOfCoupon, BigDecimal averageAmount) {
        this.year = year;
        this.month = month;
        this.sumAmountsOfCoupon = sumAmountsOfCoupon;
        this.averageAmount = averageAmount;
    }
}
