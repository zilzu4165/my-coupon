package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.application.service.CouponAnalyzeResult;

import java.math.BigDecimal;
import java.time.Month;

public class CouponAnalyzeResponse {
    public final Integer year;
    public final Month month;
    @JsonProperty("sum_amount")
    public final BigDecimal sumAmount;
    @JsonProperty("average_amount")
    public final BigDecimal averageAmount;

    public CouponAnalyzeResponse(CouponAnalyzeResult result) {
        this.year = result.year;
        this.month = result.month;
        this.sumAmount = result.sumAmountsOfCoupon;
        this.averageAmount = result.averageAmount;
    }
}
