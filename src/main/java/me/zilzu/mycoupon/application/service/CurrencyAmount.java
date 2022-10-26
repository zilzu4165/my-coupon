package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;

import java.math.BigDecimal;
import java.util.Arrays;

public class CurrencyAmount {
    public Currency currency;
    public BigDecimal amount;

    @Override
    public String toString() {
        return "CurrencyAmount{" +
                "currency=" + currency +
                ", amount=" + amount +
                '}';
    }
}
