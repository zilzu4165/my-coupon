package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Month;

public class CouponAnalyzeResponse {
    public final String message;
    public final Integer year;
    public final Month month;
    @JsonProperty("sum_amount")
    public final BigDecimal sumAmount;
    @JsonProperty("average_amount")
    public final BigDecimal averageAmount;

    public CouponAnalyzeResponse(String message, Integer year, Month month, BigDecimal sumAmount, BigDecimal averageAmount) {
        this.message = message;

        this.year = year;
        this.month = month;
        this.sumAmount = sumAmount;
        this.averageAmount = averageAmount;
    }
}
