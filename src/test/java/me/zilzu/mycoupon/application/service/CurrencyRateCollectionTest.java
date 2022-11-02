package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyRateCollectionTest {

    @Test
    @DisplayName("RestTemplate을 활용하여, 일자별 통화와 환율데이터를 수집한다. (기준통화: USD)")
    void rate_with_currency_link_by_API() {
        RestTemplate restTemplate = new RestTemplate();
        String rateByBaseAndDateUrl = "https://api.vatcomply.com/rates?date=2022-04-05&base=USD";
        ResponseEntity<String> response
                = restTemplate.getForEntity(rateByBaseAndDateUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("response = " + response);

        RateByBaseCurrency rateByBaseCurrency
                = restTemplate.getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
        assertThat(rateByBaseCurrency).isNotNull();
        System.out.println("rateByBaseCurrency = " + rateByBaseCurrency);


        final Map<Currency, BigDecimal> dataMap = rateByBaseCurrency.rates;
        assertThat(dataMap).isNotNull();

        List<CurrencyRateHistoryEntity> collect = dataMap.keySet().stream()
                .map(currency
                        -> new CurrencyRateHistoryEntity(rateByBaseCurrency.date,
                        currency, dataMap.get(currency)))
                .collect(Collectors.toList());
        collect.forEach(System.out::println);
        assertThat(collect.size()).isEqualTo(32L); // API에서 제공하는 통화 개수
    }

    @Test
    @DisplayName("2022년 9월 한달 간의 통화별 환율 데이터를 수집한다")
    void rates_collect_one_month() {
        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);

        SameMonthDatesFinder sameMonthDatesFinder = new SameMonthDatesFinder();
        CouponAnalyzeService sut = new CouponAnalyzeService(null, new LastBusinessDayRateFinder(), recentlyCreatedCouponFinder, rateOfDateFinder, couponStatsSummarizer);

        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);
        List<RateByBaseCurrency> list = sut.getRateByBaseCurrencyByAPI(septemberDates);
        assertThat(list.size()).isEqualTo(22);
        list.forEach(System.out::println);
        //TemporalAdjuster addADay = temporal -> temporal.plus(1L, ChronoUnit.DAYS);
    }
}