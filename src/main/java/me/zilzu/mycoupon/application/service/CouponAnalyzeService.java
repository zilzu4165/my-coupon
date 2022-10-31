package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponAnalyzeService {
    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    public CouponAnalyzeService(CurrencyRateHistoryRepository currencyRateHistoryRepository) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
    }

    public void save(RateByBaseCurrency rateByBaseCurrency) {
        final Map<Currency, BigDecimal> currencyRateMap = rateByBaseCurrency.rates;
        List<CurrencyRateHistoryEntity> list = currencyRateMap.keySet().stream()
                .map(currency
                        -> new CurrencyRateHistoryEntity(rateByBaseCurrency.date,
                        currency, currencyRateMap.get(currency)))
                .collect(Collectors.toList());

        currencyRateHistoryRepository.saveAll(list);
    }

    public List<RateByBaseCurrency> getRateByBaseCurrencyByAPI(List<LocalDate> septemberDates) {
        return septemberDates.parallelStream()
                .map(localDate -> {
                    RestTemplate restTemplate = new RestTemplate();
                    String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rateByBaseAndDateUrl = "https://api.vatcomply.com/rates?date=" + date + "&base=USD";
                    //System.out.println("rateByBaseAndDateUrl = " + rateByBaseAndDateUrl);
                    return restTemplate.getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
                }).collect(Collectors.toList());
    }
}
