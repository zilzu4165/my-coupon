package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.enums.Currency;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class CurrencyRateHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public LocalDate date;
    public Currency currency;
    @Column(precision = 26, scale = 16)
    public BigDecimal amount;

    public CurrencyRateHistoryEntity() {
    }

    public CurrencyRateHistoryEntity(LocalDate date, Currency currency, BigDecimal amount) {
        this.date = date;
        this.currency = currency;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CurrencyRateHistoryEntity{" +
                "id=" + id +
                ", date=" + date +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}
