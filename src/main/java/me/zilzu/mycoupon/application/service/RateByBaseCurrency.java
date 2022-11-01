package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;


public class RateByBaseCurrency {
    public LocalDate date;
    public Currency base;
    public Map<Currency, BigDecimal> rates;

    @Override
    public String toString() {
        return "RateByBaseCurrency{" +
                "date=" + date +
                ", base=" + base +
                ", rates=" + rates +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RateByBaseCurrency that = (RateByBaseCurrency) o;

        if (!Objects.equals(date, that.date)) return false;
        if (base != that.base) return false;
        return Objects.equals(rates, that.rates);
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (base != null ? base.hashCode() : 0);
        result = 31 * result + (rates != null ? rates.hashCode() : 0);
        return result;
    }
}
