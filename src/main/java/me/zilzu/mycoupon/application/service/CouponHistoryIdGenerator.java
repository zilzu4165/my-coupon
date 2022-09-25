package me.zilzu.mycoupon.application.service;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class CouponHistoryIdGenerator {
    private static final String CAPITAL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 26
    private static final String NUMERIC = "0123456789"; // 10

    // 36^20 = 1.33674945e31
    public String generate() {
        return RandomStringUtils.random(20, CAPITAL_ALPHABET + NUMERIC);
    }
}
