package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class RestTemplateTest {

    private CouponRateExchanger sut = new CouponRateExchanger();

    @Test
    @DisplayName("RestTemplate API Test")
    void test1() {
        String yearMonth = "2022-09";
        YearMonth month = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate atDay = month.atDay(1);

        int lastDayOfMonth = atDay.lengthOfMonth();
        List<CouponRateHistory> couponRateHistories = sut.rateExchangeHistory(yearMonth);

        assertThat(couponRateHistories.size()).isEqualTo(lastDayOfMonth);
        System.out.println("couponRateHistories.size() = " + couponRateHistories.size());

        List<Currency> bases = couponRateHistories
                .stream()
                .peek(System.out::println)
                .map(couponRateHistory -> couponRateHistory.base)
                .distinct()
                .collect(Collectors.toList());
        assertThat(bases.size()).isEqualTo(1);
        assertThat(bases.get(0)).isEqualTo(Currency.EUR);
    }

    @Test
    @DisplayName("해당 월의 모든 날짜 구하기")
    void test2() {
        String dateString = "2022-09";
        YearMonth yearMonth = YearMonth.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate atDay = yearMonth.atDay(1);

        LocalDate firstDay = atDay.withDayOfMonth(1);
        int lastDayOfMonth = atDay.lengthOfMonth();

        for (int i = 0; i < lastDayOfMonth; i++) {
            LocalDate plusDays = firstDay.plusDays(i);
            System.out.println(plusDays);
        }
    }
}
