package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class CouponAnalyzeServiceTest {

    @Test
    @DisplayName("RestTemplate을 활용하여, 일자별 통화와 환율데이터를 수집한다. (기준통화: USD)")
    void rate_with_currency_link_by_API() {
        RestTemplate restTemplate = new RestTemplate();
        String rateByBaseUrl = "https://api.vatcomply.com/rates?base=USD";
        ResponseEntity<String> response
                = restTemplate.getForEntity(rateByBaseUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("response = " + response);

        RateByBaseCurrency rateByBaseCurrency
                = restTemplate.getForObject(rateByBaseUrl, RateByBaseCurrency.class);
        assertThat(rateByBaseCurrency).isNotNull();
        System.out.println("rateByBaseCurrency = " + rateByBaseCurrency);


        final Map<Currency, BigDecimal> dataMap = rateByBaseCurrency.rates;
        assertThat(dataMap).isNotNull();

        List<CurrencyRateHistoryEntity> collect = dataMap.keySet().stream()
                .map(currency
                        -> new CurrencyRateHistoryEntity(rateByBaseCurrency.date,
                        currency.toString(), dataMap.get(currency)))
                .collect(Collectors.toList());
        collect.forEach(System.out::println);

//                RestTemplateBuilder.
//                builder.rootUri("some uri")
//                .additionalInterceptors((ClientHttpRequestInterceptor) (request, body, execution) -> {
//                    request.getHeaders().add("Bearer", "token");
//                    return execution.execute(request, body);
//                }).build();
    }
}