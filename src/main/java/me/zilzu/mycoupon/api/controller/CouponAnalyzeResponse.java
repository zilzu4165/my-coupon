package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.application.service.CouponAnalyzeResult;

import java.math.BigDecimal;
import java.time.YearMonth;

public class CouponAnalyzeResponse {
    @JsonProperty("target_year_month")
    public final YearMonth targetYearMonth;
    @JsonProperty("sum_amount")
    public final BigDecimal sumAmount;
    @JsonProperty("average_amount")
    public final BigDecimal averageAmount;

    public CouponAnalyzeResponse(CouponAnalyzeResult result) {
        this.targetYearMonth = YearMonth.of(result.year, result.month);
        this.sumAmount = result.sumAmountsOfCoupon;
        this.averageAmount = result.averageAmount;
    }
}
