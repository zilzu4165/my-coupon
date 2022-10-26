package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                        currency.toString(), currencyRateMap.get(currency)))
                .collect(Collectors.toList());

        currencyRateHistoryRepository.saveAll(list);
    }
}
