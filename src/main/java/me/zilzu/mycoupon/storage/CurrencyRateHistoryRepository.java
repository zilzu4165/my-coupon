package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyRateHistoryRepository extends JpaRepository<CurrencyRateHistoryEntity, Long> {
    List<CurrencyRateHistoryEntity> findByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    List<CurrencyRateHistoryEntity> findByDateAndCurrency(LocalDate localDate, Currency currency);

    List<CurrencyRateHistoryEntity> findByDateBetweenAndCurrency(LocalDate dateFrom, LocalDate dateTo, Currency currency);
}
