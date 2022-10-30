package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CouponRateExchanger {

    public List<CouponRateHistory> rateExchangeHistory(String yearMonth) {

        YearMonth month = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate atDay = month.atDay(1);

        LocalDate firstDay = atDay.withDayOfMonth(1);
        int lastDayOfMonth = atDay.lengthOfMonth();

        List<CouponRateHistory> couponRateHistories = new ArrayList<>();

        for (int i = 0; i < lastDayOfMonth; i++) {
            LocalDate plusDays = firstDay.plusDays(i);
            System.out.println(plusDays);

            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.vatcomply.com/rates?date=" + plusDays;

            CouponRateHistory rateHistory = restTemplate.getForObject(url, CouponRateHistory.class);
            couponRateHistories.add(rateHistory);
        }

        return couponRateHistories;
    }
}
