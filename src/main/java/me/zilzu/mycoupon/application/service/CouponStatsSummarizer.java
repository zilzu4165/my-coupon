package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CouponStatsSummarizer {

    private final LastBusinessDayRateFinder lastBusinessDayRateFinder;

    public CouponStatsSummarizer(LastBusinessDayRateFinder lastBusinessDayRateFinder) {
        this.lastBusinessDayRateFinder = lastBusinessDayRateFinder;
    }


    public CouponStatsSummary summarize(List<Coupon> recentlyCreatedCoupon, List<RateOfDate> rateOfDateList) {
        List<BigDecimal> convertedCouponAmountList = recentlyCreatedCoupon.parallelStream()
                .map(coupon -> {
                    LocalDate createdDate = coupon.createdTime.toLocalDate();
                    Optional<RateOfDate> maybeRateOfDate = rateOfDateList.stream()
                            .filter(rateOfDate -> rateOfDate.date.equals(createdDate))
                            .findFirst();
                    RateOfDate rateOfDate = maybeRateOfDate.orElseGet(() -> lastBusinessDayRateFinder.find(rateOfDateList, createdDate));
                    BigDecimal rate = rateOfDate.rate;
                    BigDecimal amount = BigDecimal.valueOf(coupon.amountOff);
                    return rate.multiply(amount);
                }).collect(Collectors.toList());

        BigDecimal sumAmountsOfCoupon = convertedCouponAmountList.stream()
                .reduce(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP), BigDecimal::add);
        BigDecimal averageAmount = sumAmountsOfCoupon.divide(BigDecimal.valueOf(convertedCouponAmountList.size()));

        return new CouponStatsSummary(sumAmountsOfCoupon, averageAmount);

    }
}
