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

        RateByBaseCurrency rateByBaseCurrency
                = restTemplate.getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
        assertThat(rateByBaseCurrency).isNotNull();


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
        List<LocalDate> septemberDates = sameMonthDatesFinder.find(firstDateOfMonth);
        RateHistoryCollector rateHistoryCollector = new RateHistoryCollector(null);
        List<RateByBaseCurrency> list = rateHistoryCollector.collect(septemberDates);

        // 9월의 환율 수집일은 22일이다.
        assertThat(list.size()).isEqualTo(22);

        List<RateOfDate> rateOfDateList = list.stream()
                .map(rateByBaseCurrency -> new RateOfDate(rateByBaseCurrency.date, rateByBaseCurrency.rates.get(Currency.KRW)))
                .collect(Collectors.toList());

        LocalDate bizDay01 = LocalDate.of(2022, Month.SEPTEMBER, 2);
        LocalDate holiday01 = LocalDate.of(2022, Month.SEPTEMBER, 3);
        // 평일 데이터는 존재하고 주말 데이터는 없어야 한다.
        assertThat(rateOfDateList.size()).isEqualTo(22L);
        assertThat(rateOfDateList.stream()
                .filter(rateOfDate -> rateOfDate.date.isEqual(bizDay01))
                .findFirst())
                .isNotEmpty();
        assertThat(rateOfDateList.stream()
                .filter(rateOfDate -> rateOfDate.date.isEqual(holiday01))
                .findFirst()).isEmpty();

    }
}