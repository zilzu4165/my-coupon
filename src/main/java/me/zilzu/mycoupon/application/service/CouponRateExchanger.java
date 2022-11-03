package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class CouponRateExchanger {

    public List<CouponRateHistory> getRateOfMonth(YearMonth yearMonth) {

        LocalDate atDay = yearMonth.atDay(1);

        LocalDate firstDay = atDay.withDayOfMonth(1);
        int lastDayOfMonth = atDay.lengthOfMonth();

        List<CouponRateHistory> couponRateHistories = new ArrayList<>();

        for (int i = 0; i < lastDayOfMonth; i++) {
            LocalDate plusDays = firstDay.plusDays(i);

            if (plusDays.getDayOfWeek() == DayOfWeek.SATURDAY || plusDays.getDayOfWeek() == DayOfWeek.SUNDAY) {  // 주말인 경우 패스
                continue;
            }

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.vatcomply.com/rates?date=" + plusDays;
            CouponRateHistory rateHistory = restTemplate.getForObject(url, CouponRateHistory.class);
            couponRateHistories.add(rateHistory);
        }

        return couponRateHistories;
    }


    public List<CouponRateCalculationResult> calculateRateExchanger(List<Coupon> foundCouponsOfMonth, List<CouponRateHistory> couponRateHistories) {
        /**
         * 1. foundCouponsOfMonth :: 해당 월에 생성된 모든 쿠폰
         * 2. couponRateHistories :: 해당 월의 환율
         */
        List<CouponRateCalculationResult> calculatedRateCoupons = new ArrayList<>();

        for (Coupon coupon : foundCouponsOfMonth) {
            LocalDate date = new DayConverter().convertWeekDay(coupon.createdTime.toLocalDate());

            for (CouponRateHistory couponRateHistory : couponRateHistories) {
                if (date.isEqual(couponRateHistory.date)) {
                    BigDecimal rateOfCurrency = couponRateHistory.rates.get(Currency.KRW);
                    BigDecimal rateOfKRW = rateOfCurrency.multiply(BigDecimal.valueOf(coupon.amountOff));

                    calculatedRateCoupons.add(new CouponRateCalculationResult(coupon.id, rateOfKRW, Currency.KRW, date));
                }
            }
        }
        return calculatedRateCoupons;
    }

}
