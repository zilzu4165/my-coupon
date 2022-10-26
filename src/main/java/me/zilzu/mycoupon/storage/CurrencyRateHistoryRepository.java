package me.zilzu.mycoupon.storage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRateHistoryRepository extends JpaRepository<CurrencyRateHistoryEntity, Long> {
}
