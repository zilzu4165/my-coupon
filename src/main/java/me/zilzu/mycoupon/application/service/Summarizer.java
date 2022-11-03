package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class Summarizer {

    public CouponStatsResult summarize(List<CouponRateCalculationResult> calculatedResults, Currency currency) {
        BigDecimal sum = calculatedResults.stream()
                .map(result -> result.calculatedRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(BigDecimal.valueOf(calculatedResults.size()));

        return new CouponStatsResult(sum, average, currency);
    }
}
