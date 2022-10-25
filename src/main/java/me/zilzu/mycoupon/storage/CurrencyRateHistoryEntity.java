package me.zilzu.mycoupon.storage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class CurrencyRateHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public LocalDate date;
    public String currency;
    public BigDecimal amount;

    public CurrencyRateHistoryEntity() {
    }

    public CurrencyRateHistoryEntity(LocalDate date, String currency, BigDecimal amount) {
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
