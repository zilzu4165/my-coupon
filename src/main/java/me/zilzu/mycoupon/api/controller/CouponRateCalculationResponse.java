package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.application.service.CouponRateCalculationResult;
import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CouponRateCalculationResponse {

    public CouponId id;
    @JsonProperty(value = "calculated_rate")
    public BigDecimal calculatedRate;
    public Currency currency;
    public LocalDate date;

    public CouponRateCalculationResponse(CouponRateCalculationResult result) {
        this.id = result.id;
        this.calculatedRate = result.calculatedRate;
        this.currency = result.currency;
        this.date = result.date;
    }
}
