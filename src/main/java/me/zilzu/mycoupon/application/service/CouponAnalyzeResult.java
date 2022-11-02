package me.zilzu.mycoupon.application.service;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;

public class CouponAnalyzeResult {
    public final Integer year;
    public final Month month;
    public final BigDecimal sumAmountsOfCoupon;
    public final BigDecimal averageAmount;

    public CouponAnalyzeResult(YearMonth targetYearMonth, BigDecimal sumAmountsOfCoupon, BigDecimal averageAmount) {
        this.year = targetYearMonth.getYear();
        this.month = targetYearMonth.getMonth();
        this.sumAmountsOfCoupon = sumAmountsOfCoupon;
        this.averageAmount = averageAmount;
    }
}
