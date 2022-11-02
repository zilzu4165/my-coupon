package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RateOfDateFinder {
    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    public RateOfDateFinder(CurrencyRateHistoryRepository currencyRateHistoryRepository) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
    }

    public List<RateOfDate> find(LocalDate firstDateOfMonth, LocalDate lastDateOfMonth, Currency currency) {
        List<CurrencyRateHistoryEntity> targetRates2 = currencyRateHistoryRepository.findByDateBetweenAndCurrency(firstDateOfMonth, lastDateOfMonth, currency);
        return targetRates2.stream()
                .map(currencyRateHistoryEntity -> new RateOfDate(currencyRateHistoryEntity.date, currencyRateHistoryEntity.amount))
                .collect(Collectors.toList());
    }
}
