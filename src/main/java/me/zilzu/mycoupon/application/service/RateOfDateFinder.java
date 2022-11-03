package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RateOfDateFinder {
    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    private final SameMonthDatesFinder sameMonthDatesFinder;

    private final RateHistoryCollector rateHistoryCollector;

    public RateOfDateFinder(CurrencyRateHistoryRepository currencyRateHistoryRepository,
                            SameMonthDatesFinder sameMonthDatesFinder,
                            RateHistoryCollector rateHistoryCollector) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
        this.sameMonthDatesFinder = sameMonthDatesFinder;
        this.rateHistoryCollector = rateHistoryCollector;
    }

    public List<RateOfDate> find(YearMonth targetYearMonth, Currency currency) {
        LocalDate firstDateOfMonth = LocalDate.of(targetYearMonth.getYear(), targetYearMonth.getMonth(), 1);
        LocalDate lastDateOfMonth = LocalDate.of(targetYearMonth.getYear(), targetYearMonth.getMonth(), firstDateOfMonth.lengthOfMonth());

        List<CurrencyRateHistoryEntity> rateHistoryList = currencyRateHistoryRepository.findByDateBetweenAndCurrency(firstDateOfMonth, lastDateOfMonth, currency);

        if (rateHistoryList.isEmpty()) {
            List<LocalDate> dates = sameMonthDatesFinder.find(firstDateOfMonth);
            List<RateByBaseCurrency> rateList = rateHistoryCollector.collect(dates);
            rateHistoryCollector.save(rateList);
            rateHistoryList = currencyRateHistoryRepository.findByDateBetweenAndCurrency(firstDateOfMonth, lastDateOfMonth, currency);
        }

        return rateHistoryList.stream()
                .map(currencyRateHistoryEntity -> new RateOfDate(currencyRateHistoryEntity.date, currencyRateHistoryEntity.amount))
                .collect(Collectors.toList());
    }

}
