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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    }

    @Test
    @DisplayName("2022년 9월 한달 간의 통화별 환율 데이터를 수집한다")
    void rates_collect_one_month() {

        LocalDate localDate = LocalDate.of(2022, 9, 1);
        List<LocalDate> septemberDates = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            if (!localDate.getMonth().equals(Month.SEPTEMBER)) break;
            septemberDates.add(localDate);
            localDate = localDate.plusDays(1L);
        }

        CouponAnalyzeService sut = new CouponAnalyzeService(null);
        List<RateByBaseCurrency> list = sut.getRateByBaseCurrencyByAPI(septemberDates);
        assertThat(list.size()).isEqualTo(30);
        list.forEach(System.out::println);

        //TemporalAdjuster addADay = temporal -> temporal.plus(1L, ChronoUnit.DAYS);
    }

    @Test
    @DisplayName("해당 월의 LocalDate 리스트를 반환한다.")
    void retreive_all_loacldate_of_month() {

        LocalDate firstDateOfMonth = LocalDate.of(2022, Month.SEPTEMBER, 1);
        List<LocalDate> septemberDates = getDatesOfMonthStream(firstDateOfMonth);
        septemberDates.forEach(System.out::println);
    }

    private List<LocalDate> getDatesOfMonthStream(LocalDate firstDateOfMonth) {
        return IntStream.range(0, firstDateOfMonth.lengthOfMonth())
                .mapToObj(firstDateOfMonth::plusDays)
                .collect(Collectors.toList());
    }

}